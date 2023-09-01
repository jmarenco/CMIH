package general;

import java.text.DecimalFormat;

import ilog.concert.IloException;

public class SeparadorPartitioned extends SeparadorGenerico
{
	private static double _epsilon = 1e-4;
	private static double _umbral = 0.25;
	private static double _profundidad = 0.5;
	
	private static int _activaciones = 0;
	private static int _intentos = 0;
	private static int _cortes = 0;
	private static double _tiempo = 0;
	private static boolean _activo = true;
	
	public SeparadorPartitioned(Separador padre)
	{
		super(padre);
	}

	@Override
	public void run(Solucion solucion) throws IloException
	{
		if( _activo == false )
			return;
		
		double inicio = System.currentTimeMillis();
		for(int h=0; h<_instancia.cantidadHiperaristas(); ++h)
		{
			if( solucion.zVar(h) < _umbral || solucion.hiperaristaEntera(h) )
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
				
				if( solucion.zVar(h) > rhs + _profundidad + _epsilon )
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
				
					if( dv.getLHS() <= _profundidad + 2 * _epsilon )
						System.err.println("**** SeparatorPartitioned: desigualdad no violada!");

					dv.setRHS(Desigualdad.Operador.LE, 0.0);
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
		System.out.print(" -> Partitioned = Act: " + _activaciones);
		System.out.print(", Intentos: " + _intentos);
		System.out.println(", Cortes: " + _cortes);
	}
	
	public static void mostrarResumen()
	{
		System.out.print("P: " + _cortes + "/" + _activaciones + " (" + new DecimalFormat("####0.0").format(_tiempo) + " sec) | ");
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
	
	public static void setProfundidad(double valor)
	{
		_profundidad = valor;
	}
}