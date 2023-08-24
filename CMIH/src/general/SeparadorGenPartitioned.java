package general;

import ilog.concert.IloException;

public class SeparadorGenPartitioned extends SeparadorGenerico
{
	private static double _epsilon = 1e-4;
	private static double _umbral = 0.05;
	
	private static int _activaciones = 0;
	private static int _intentos = 0;
	private static int _cortes = 0;
	
	public SeparadorGenPartitioned(Separador padre)
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
			
			double rhs = 0;
			int[] D = new int[_c];
			
			for(int d=0; d<_c; ++d)
			{
				int midx = 0;
				for(int i=1; i<hiperarista.size(); ++i)
				{
					if( solucion.xVar(hiperarista.get(i), d) < solucion.xVar(hiperarista.get(midx), d) )
						midx = i;
				}
				
				D[d] = midx;
				rhs += solucion.xVar(hiperarista.get(midx), d);
			}
				
			if( solucion.zVar(h) > rhs + _epsilon )
			{
				Desigualdad dv = new Desigualdad(_modelo, solucion);
				dv.agregar(h, 1.0);
					
				for(int d=0; d<_c; ++d)
					dv.agregar(hiperarista.get(D[d]), d, -1.0);
				
				if( dv.getLHS() <= 2 * _epsilon )
					System.err.println("**** SeparatorGenPartitioned: desigualdad no violada!");

				dv.setRHS(Desigualdad.Operador.LE, 0.0);
				_padre.agregar( dv.getIloExpr(_cplex) );
				++_cortes;
			}
			
			++_intentos;
		}

		++_activaciones;
	}
	
	public static void mostrarEstadisticas()
	{
		System.out.print(" -> Gen-Partitioned = Act: " + _activaciones);
		System.out.print(", Intentos: " + _intentos);
		System.out.println(", Cortes: " + _cortes);
	}
	
	public static void mostrarResumen()
	{
		System.out.print("GP: " + _cortes + "/" + _activaciones + " | ");
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