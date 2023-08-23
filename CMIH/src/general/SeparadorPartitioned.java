package general;

import ilog.concert.IloException;

public class SeparadorPartitioned extends SeparadorGenerico
{
	private static double _epsilon = 1e-4;
	private static double _umbral = 0.05;
	
	private static int _activaciones = 0;
	private static int _intentos = 0;
	private static int _cortes = 0;
	
	public SeparadorPartitioned(Separador padre)
	{
		super(padre);
	}

	@Override
	public void run(Solucion solucion) throws IloException
	{
		for(int h=0; h<_instancia.cantidadHiperaristas(); ++h)
		{
			if( solucion.zVar(h) < _umbral )
				continue;

			Hiperarista hiperarista = _instancia.getHiperarista(h);
			
			for(int ix = 0; ix < hiperarista.size(); ++ix)
			for(int jx = ix+1; jx < hiperarista.size(); ++jx)
			{
				int i = hiperarista.get(ix);
				int j = hiperarista.get(jx);

				double rhs = 0;
				boolean[] D = new boolean[_c];
			
				for(int d=0; d<_c; ++d)
				{
					if( solucion.xVar(i,d) < solucion.xVar(j,d) )
					{
						D[d] = false;
						rhs += solucion.xVar(i,d);
					}
					else
					{
						D[d] = true;
						rhs += solucion.xVar(j,d);
					}
				}
				
				if( solucion.zVar(h) > rhs + _epsilon )
				{
					Desigualdad dv = new Desigualdad(_modelo, solucion);
					dv.agregar(h, 1.0);
					
					for(int d=0; d<_c; ++d)
					{
						if( D[d] == false )
							dv.agregar(i, d, -1.0);
						else
							dv.agregar(j, d, -1.0);
					}
				
					if( dv.getLHS() <= 2 * _epsilon )
						System.err.println("**** SeparatorPartitioned: desigualdad no violada!");

					dv.setRHS(Desigualdad.Operador.LE, 0.0);
					_padre.agregar( dv.getIloExpr(_cplex) );
					++_cortes;
				}
			}
			
			++_intentos;
		}

		++_activaciones;
	}
	
	public static void mostrarEstadisticas()
	{
		System.out.print(" -> Partitioned = Act: " + _activaciones);
		System.out.print(", Intentos: " + _intentos);
		System.out.println(", Cortes: " + _cortes);
	}
	
	public static void mostrarResumen()
	{
		System.out.print("P: " + _cortes + "/" + _activaciones + " |");
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