package general;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ilog.concert.IloException;

@Deprecated
public class SeparadorDosCliqueAnterior extends SeparadorGenerico
{
	private static double _epsilon = 1e-4;
	private static double _umbral = 0.25;
	
	private static int _activaciones = 0;
	private static int _intentos = 0;
	private static int _cortes = 0;
	private static double _tiempo = 0;
	
	private ArrayList<Par> _pares;
	
	public static class Par
	{
		public int indice1;
		public int indice2;
		
		public Hiperarista hiperarista1;
		public Hiperarista hiperarista2;
		
		public Par(Instancia instancia, int i, int j)
		{
			indice1 = i;
			indice2 = j;

			hiperarista1 = instancia.getHiperarista(i);
			hiperarista2 = instancia.getHiperarista(j);
		}
	}
	
	public SeparadorDosCliqueAnterior(Separador padre)
	{
		super(padre);
		_pares = new ArrayList<Par>();
		
		// Busca todos los pares de hiperaristas vecinas
		for(int i=0; i<_instancia.cantidadHiperaristas(); ++i)
		for(int j=i+1; j<_instancia.cantidadHiperaristas(); ++j)
		{
			if( _instancia.getHiperarista(i).vecina( _instancia.getHiperarista(j), _instancia) )
				_pares.add(new Par(_instancia, i, j));
		}
	}

	@Override
	public void run(Solucion solucion) throws IloException
	{
		double inicio = System.currentTimeMillis();
		for(Par par: _pares)
		{
			if( solucion.zVar(par.indice1) + solucion.zVar(par.indice2) < _umbral )
				continue;

			for(int c=0; c<_c; ++c)
			for(Integer i: par.hiperarista1.getVertices())
			for(Integer j: par.hiperarista2.getVertices())
			{
				if( solucion.zVar(par.indice1) + solucion.zVar(par.indice2) + solucion.xVar(i, c) + solucion.xVar(j, c) > 3 + _epsilon )
				{
					Desigualdad dv = new Desigualdad(_modelo, solucion);

					dv.agregar(par.indice1, 1.0);
					dv.agregar(par.indice2, 1.0);
					dv.agregar(i, c, 1.0);
					dv.agregar(j, c, 1.0);
					
					if( dv.getLHS() <= 3 - _epsilon )
						System.err.println("**** SeparatorDosClique: desigualdad no violada!");

					dv.setRHS(Desigualdad.Operador.LE, 3.0);
					_padre.agregar( dv.getIloExpr(_cplex) );
					++_cortes;
				}
			}
			
			++_intentos;
		}

		_activaciones++;
		_tiempo += (System.currentTimeMillis() - inicio) / 1000.0;
	}
	
	public static void mostrarEstadisticas()
	{
		System.out.print(" -> Dos Clique = Act: " + _activaciones);
		System.out.print(", Intentos: " + _intentos);
		System.out.println(", Cortes: " + _cortes);
	}
	
	public static void mostrarResumen()
	{
		System.out.print("2C: " + _cortes + "/" + _activaciones + " (" + new DecimalFormat("####0.0").format(_tiempo) + " sec) | ");
	}
	
	public static int cortes()
	{
		return _cortes;
	}

	public static void inicializarEstadisticas()
	{
		_activaciones = 0;
		_intentos = 0;
		_cortes = 0;
		_tiempo = 0;
	}
}