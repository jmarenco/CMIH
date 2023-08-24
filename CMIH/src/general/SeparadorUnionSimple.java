package general;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

import ilog.concert.IloException;

public class SeparadorUnionSimple extends SeparadorGenerico
{
	private static double _epsilon = 1e-4;
	private static double _umbral = 0.05;
	
	private static int _activaciones = 0;
	private static int _intentos = 0;
	private static int _cortes = 0;
	
	public SeparadorUnionSimple(Separador padre)
	{
		super(padre);
	}

	@Override
	public void run(Solucion solucion) throws IloException
	{
		for(int h=0; h<_instancia.cantidadHiperaristas(); ++h)
		{
//			if( solucion.zVar(h) < _umbral )
//				continue;

			Hiperarista hiperarista = _instancia.getHiperarista(h);
			
			for(int c=0; c<_c; ++c)
			for(int ix = 0; ix < hiperarista.size(); ++ix)
			for(int jx = ix+1; jx < hiperarista.size(); ++jx)
			{
				int v = hiperarista.get(ix);
				int w = hiperarista.get(jx);
				
				if( solucion.xVar(v, c) < _umbral || solucion.xVar(w, c) < _umbral)
					continue;
				
				double lhs = solucion.zVar(h) + solucion.xVar(w, c) + solucion.xVar(v, c);

				ArrayList<Integer> clique = new ArrayList<Integer>();
				clique.add(v);
				
				for(Integer k: verticesOrdenados(solucion, c))
				{
					if( !hiperarista.contiene(k) && todosVecinos(clique, k) )
					{
						clique.add(k);
						lhs += solucion.xVar(k, c);
					}
				}
				
				if( lhs > 2 + _epsilon )
				{
					Desigualdad dv = new Desigualdad(_modelo, solucion);
					dv.agregar(h, 1.0);
					dv.agregar(w, c, 1.0);
					
					for(Integer k: clique)
						dv.agregar(k, c, 1.0);
					
					if( dv.getLHS() <= 2 - _epsilon )
						System.err.println("**** SeparatorUnionSimple: desigualdad no violada!");

					dv.setRHS(Desigualdad.Operador.LE, 2.0);
					_padre.agregar( dv.getIloExpr(_cplex) );
					++_cortes;
				}
			}
			
			++_intentos;
		}

		++_activaciones;
	}
	
	private ArrayList<Integer> verticesOrdenados(Solucion solucion, int color)
	{
		ArrayList<Integer> ret = new ArrayList<Integer>(_n);
		for(int i=0; i<_n; ++i)
			ret.add(i);
		
		Collections.sort(ret, (i,j) -> (int)Math.signum(solucion.xVar(j,color) - solucion.xVar(i,color)));
		return ret;
	}
	
	private boolean todosVecinos(ArrayList<Integer> clique, int vertice)
	{
		return clique.stream().allMatch(v -> _instancia.getArista(v, vertice));
	}
	
	public static void mostrarEstadisticas()
	{
		System.out.print(" -> Union Simple = Act: " + _activaciones);
		System.out.print(", Intentos: " + _intentos);
		System.out.println(", Cortes: " + _cortes);
	}
	
	public static void mostrarResumen()
	{
		System.out.print("US: " + _cortes + "/" + _activaciones + " | ");
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
	}
}