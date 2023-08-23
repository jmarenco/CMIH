package general;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public abstract class SeparadorGenerico
{
	protected IloCplex _cplex;
	protected Instancia _instancia;
	protected Modelo _modelo;
	protected Separador _padre;
	protected int _n;
	protected int _c;
	protected int _h;

	public SeparadorGenerico(Separador padre)
	{
		_padre = padre;
		_modelo = padre.getModelo();
		_cplex = _modelo.getCplex();
		_instancia = _modelo.getInstancia();
		_n = _instancia.getVertices();
		_c = _instancia.getColores();
		_h = _instancia.cantidadHiperaristas();
	}
	
	public abstract void run(Solucion solucion) throws IloException;
}