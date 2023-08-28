package general;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import ilog.concert.IloException;

public class SeparadorTresClique extends SeparadorGenerico
{
	private static double _epsilon = 1e-4;
	private static double _umbral = 0.25;
	
	private static int _activaciones = 0;
	private static int _intentos = 0;
	private static int _cortes = 0;
	private static double _tiempo = 0;
	private static boolean _activo = true;
	
	private ArrayList<Triangulo> _triangulos;
	
	public static class Triangulo
	{
		public int indice1;
		public int indice2;
		public int indice3;
		
		public Hiperarista hiperarista1;
		public Hiperarista hiperarista2;
		public Hiperarista hiperarista3;
		
		public Triangulo(Instancia instancia, int i, int j, int k)
		{
			indice1 = i;
			indice2 = j;
			indice3 = k;

			hiperarista1 = instancia.getHiperarista(i);
			hiperarista2 = instancia.getHiperarista(j);
			hiperarista3 = instancia.getHiperarista(k);
		}
	}
	
	public SeparadorTresClique(Separador padre)
	{
		super(padre);
		
		if( _activo == false )
			return;
		
		_triangulos = new ArrayList<Triangulo>();
		
		// Busca todos los pares de hiperaristas vecinas
		for(int i=0; i<_instancia.cantidadHiperaristas(); ++i)
		for(int j=i+1; j<_instancia.cantidadHiperaristas(); ++j)
		for(int k=j+1; k<_instancia.cantidadHiperaristas(); ++k)
		{
			if( _instancia.getHiperarista(i).vecina( _instancia.getHiperarista(j), _instancia) &&
		        _instancia.getHiperarista(i).vecina( _instancia.getHiperarista(k), _instancia) &&
		        _instancia.getHiperarista(j).vecina( _instancia.getHiperarista(k), _instancia) )
			{
				_triangulos.add(new Triangulo(_instancia, i, j, k));
			}
		}
	}

	@Override
	public void run(Solucion solucion) throws IloException
	{
		if( _activo == false )
			return;

		double inicio = System.currentTimeMillis();
		for(Triangulo triangulo: _triangulos)
		{
			if( solucion.zVar(triangulo.indice1) + solucion.zVar(triangulo.indice2) + solucion.zVar(triangulo.indice3) < _umbral )
				continue;
			
			if( solucion.hiperaristaEntera(triangulo.indice1) || solucion.hiperaristaEntera(triangulo.indice2) || solucion.hiperaristaEntera(triangulo.indice3) )
				continue;

			for(int c1=0; c1<_c; ++c1)
			for(int c2=0; c2<_c; ++c2)
			{
				final int color1 = c1;
				final int color2 = c2;

				int i = Collections.max(triangulo.hiperarista1.getVertices(), (v,w) -> (int)Math.signum(solucion.xVar(v, color1) + solucion.xVar(v, color2) - solucion.xVar(w, color1) - solucion.xVar(w, color2)));
				int j = Collections.max(triangulo.hiperarista2.getVertices(), (v,w) -> (int)Math.signum(solucion.xVar(v, color1) + solucion.xVar(v, color2) - solucion.xVar(w, color1) - solucion.xVar(w, color2)));
				int k = Collections.max(triangulo.hiperarista3.getVertices(), (v,w) -> (int)Math.signum(solucion.xVar(v, color1) + solucion.xVar(v, color2) - solucion.xVar(w, color1) - solucion.xVar(w, color2)));

				if( solucion.zVar(triangulo.indice1) + solucion.zVar(triangulo.indice2) + solucion.zVar(triangulo.indice3) + solucion.xVar(i, c1) + solucion.xVar(i, c2) + solucion.xVar(j, c1) + solucion.xVar(j, c2) + solucion.xVar(k, c1) + solucion.xVar(k, c2) > 5 + _epsilon )
				{
					Desigualdad dv = new Desigualdad(_modelo, solucion);

					dv.agregar(triangulo.indice1, 1.0);
					dv.agregar(triangulo.indice2, 1.0);
					dv.agregar(triangulo.indice3, 1.0);
					dv.agregar(i, c1, 1.0);
					dv.agregar(i, c2, 1.0);
					dv.agregar(j, c1, 1.0);
					dv.agregar(j, c2, 1.0);
					dv.agregar(k, c1, 1.0);
					dv.agregar(k, c2, 1.0);
					
					if( dv.getLHS() <= 5 - _epsilon )
						System.err.println("**** SeparatorTresClique: desigualdad no violada!");

					dv.setRHS(Desigualdad.Operador.LE, 5.0);
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
		System.out.print(" -> Tres Clique = Act: " + _activaciones);
		System.out.print(", Intentos: " + _intentos);
		System.out.println(", Cortes: " + _cortes);
	}
	
	public static void mostrarResumen()
	{
		System.out.print("3C: " + _cortes + "/" + _activaciones + " (" + new DecimalFormat("####0.0").format(_tiempo) + " sec) | ");
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
	
	public static void setActive(boolean valor)
	{
		_activo = valor;
	}
}