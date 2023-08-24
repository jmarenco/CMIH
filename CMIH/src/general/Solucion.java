package general;

import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;

// Representa una solucion (fraccionaria o entera)
public class Solucion
{
	private Instancia _instancia;
	private double[][] _xVar;
	private double[] _zVar;
	
	// Constructor
	public Solucion(Modelo modelo)
	{
		_instancia = modelo.getInstancia();
		
		_xVar = new double[_instancia.getVertices()][_instancia.getColores()];
		_zVar = new double[_instancia.cantidadHiperaristas()];
		
		try
		{
			if( modelo.getCplex().getStatus() == Status.Optimal || modelo.getCplex().getStatus() == Status.Feasible )
			{
				for(int i=0; i<_instancia.getVertices(); ++i)
				for(int j=0; j<_instancia.getColores(); ++j)
					_xVar[i][j] = modelo.getCplex().getValue(modelo.xVar(i, j));
					
				for(int h=0; h<_instancia.cantidadHiperaristas(); ++h)
					_zVar[h] = modelo.getCplex().getValue(modelo.zVar(h));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// Constructor
	public Solucion(Separador separador)
	{
		Modelo modelo = separador.getModelo();
		_instancia = modelo.getInstancia();
		
		_xVar = new double[_instancia.getVertices()][_instancia.getColores()];
		_zVar = new double[_instancia.cantidadHiperaristas()];
		
		try
		{
			for(int i=0; i<_instancia.getVertices(); ++i)
			for(int j=0; j<_instancia.getColores(); ++j)
				_xVar[i][j] = separador.getValor(modelo.xVar(i, j));
					
			for(int h=0; h<_instancia.cantidadHiperaristas(); ++h)
				_zVar[h] = separador.getValor(modelo.zVar(h));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// Consulta el valor de las variables
	public double xVar(int v, int c)
	{
		return _xVar[v][c];
	}
	public double zVar(int h)
	{
		return _zVar[h];
	}
	
	// Imprime la solución
	public void imprimir()
	{
		for(int i=0; i<_instancia.getVertices(); ++i)
		for(int j=0; j<_instancia.getColores(); ++j) if( xVar(i,j) != 0 )
			System.out.println("x[" + i + "," + j + "] = " + xVar(i,j));
			
		for(int h=0; h<_instancia.cantidadHiperaristas(); ++h) if( zVar(h) != 0 )
			System.out.println("z[" + h + "] = " + zVar(h));
	}
}