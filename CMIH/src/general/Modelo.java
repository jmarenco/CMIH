package general;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Modelo
{
	// Instancia asociada
	private Instancia _instancia;
	
	// Instancia de cplex
	private IloCplex _cplex;
	
	// Cantidad de vertices y colores
	private int _n;
	private int _c;
	private int _cantidadHiperaristas;
	
	// Variables
	private IloNumVar[][] _x;
	private IloNumVar[] _z;
	
	// Constructor
	public Modelo(Instancia instancia)
	{
		_instancia = instancia;
		
		_n = _instancia.getVertices();
		_c = _instancia.getColores();
		_cantidadHiperaristas = _instancia.cantidadHiperaristas();
		
		_x = new IloNumVar[_n][_c];
		_z = new IloNumVar[_cantidadHiperaristas];
	}
	
	// Cantidad de variables
	public int variables()
	{
		return _n * _c + _cantidadHiperaristas;
	}
	
	// Cantidad de restricciones
	public int restricciones()
	{
		return _n + _n * _c + _instancia.paresTotales() * _c;
	}
	
	// Variable x
	public IloNumVar xVar(int vertice, int color)
	{
		return _x[vertice][color];
	}
	
	// Variable y
	public IloNumVar zVar(int h)
	{
		return _z[h];
	}
	
	// Variables
	public IloNumVar[][] getXVariables()
	{
		return _x;
	}
	public IloNumVar[] getZVariables()
	{
		return _z;
	}
	
	// Genera el modelo inicial
	public IloCplex crear() throws IloException
	{
		_cplex = new IloCplex();
		
		// Define the variables one-by-one
		for(int i=0; i<_n; ++i)
		for(int j=0; j<_c; ++j)
			_x[i][j] = _cplex.boolVar("x" + i + "_" + j);
		
		for(int h=0; h<_cantidadHiperaristas; ++h)
			_z[h] = _cplex.boolVar("z" + h);

		// Objective function
		IloNumExpr fobj = _cplex.linearIntExpr();
		for(int h=0; h<_cantidadHiperaristas; ++h)
			fobj = _cplex.sum(fobj, zVar(h));
		
		_cplex.addMaximize(fobj);

		for(int i=0; i<_n; ++i)
		{
			IloNumExpr lhs = _cplex.linearIntExpr();
			for(int j=0; j<_c; ++j)
				lhs = _cplex.sum(lhs, xVar(i,j));

			_cplex.addEq(lhs, 1, "ex" + i);
		}
		
		for(int i=0; i<_n; ++i)
		for(int j=i+1; j<_n; ++j) if( _instancia.getArista(i,j) == true )
		for(int c=0; c<_c; ++c)
		{
			IloNumExpr lhs = _cplex.linearIntExpr();

			lhs = _cplex.sum(lhs, xVar(i,c));
			lhs = _cplex.sum(lhs, xVar(j,c));
			
			_cplex.addLe(lhs, 1, "ed" + i + "_" + j + "_" + c);
		}
		
		for(int h=0; h<_cantidadHiperaristas; ++h)
		for(int i=0; i<_instancia.getHiperarista(h).size(); ++i)
		for(int j=i+1; j<_instancia.getHiperarista(h).size(); ++j)
		for(int c=0; c<_c; ++c)
		{
			Hiperarista hiperarista = _instancia.getHiperarista(h);
			IloNumExpr lhs = _cplex.linearIntExpr();

			lhs = _cplex.sum(lhs, zVar(h));
			lhs = _cplex.sum(lhs, _cplex.prod(-1, xVar(hiperarista.getVertices().get(i),c)));
			lhs = _cplex.sum(lhs, xVar(hiperarista.getVertices().get(j),c));
			
			_cplex.addLe(lhs, 1, "zc" + h + "_" + i + "_" + j + "_" + c);
		}
		
		return _cplex;
	}
	
	public IloCplex getCplex()
	{
		return _cplex;
	}
	
	public Instancia instancia()
	{
		return _instancia;
	}
}