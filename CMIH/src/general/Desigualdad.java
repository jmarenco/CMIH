package general;

import java.util.ArrayList;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

public class Desigualdad 
{
	public class TerminoX
	{
		public int vertice;
		public int color;
		public double coeficiente;
	}
	
	public class TerminoZ
	{
		public int hiperarista;
		public double coeficiente;
	}

	private Modelo _modelo;
	private Solucion _solucion;
	private ArrayList<TerminoX> _xTerms;
	private ArrayList<TerminoZ> _zTerms;
	private Operador _operador;
	private double _rhs;

	public Desigualdad(Modelo modelo, Solucion solucion)
	{
		_modelo = modelo;
		_solucion = solucion;
		_xTerms = new ArrayList<TerminoX>();
		_zTerms = new ArrayList<TerminoZ>();
		_operador = Operador.Ninguno;
	}
	
	public void agregar(int vertice, int color, double coeficiente)
	{
		TerminoX termino = new TerminoX();
		termino.vertice = vertice;
		termino.color = color;
		termino.coeficiente = coeficiente;
		
		_xTerms.add(termino);
	}
	
	public void agregar(int hiperarista, double coeficiente)
	{
		TerminoZ termino = new TerminoZ();
		termino.hiperarista = hiperarista;
		termino.coeficiente = coeficiente;
		
		_zTerms.add(termino);
	}
	
	public double getLHS()
	{
		double ret = 0;

		for(TerminoX termino: _xTerms)
			ret += termino.coeficiente * _solucion.xVar(termino.vertice, termino.color);

		for(TerminoZ termino: _zTerms)
			ret += termino.coeficiente * _solucion.zVar(termino.hiperarista);
		
		return ret;
	}
	
	public void setRHS(Operador operador, double rhs)
	{
		_operador = operador;
		_rhs = rhs;
	}
	
	public double getRHS()
	{
		return _rhs;
	}
	
	public double violacion()
	{
		if( _operador == Operador.LE )
			return getLHS() - _rhs;
		
		if( _operador == Operador.GE )
			return _rhs - getLHS();
		
		return Math.abs(getLHS() - _rhs);
	}
	
	public enum Operador { LE, GE, EQ, Ninguno };
	
	public IloRange getIloExpr(IloCplex cplex) throws IloException
	{
		if( _operador == Operador.Ninguno )
			throw new RuntimeException("Desigualdad.getIloExpr(), _operador == null");
		
		IloNumExpr lhs = cplex.linearNumExpr();

		for(TerminoX termino: _xTerms)
			lhs = cplex.sum(lhs, cplex.prod(termino.coeficiente, _modelo.xVar(termino.vertice, termino.color)));
		
		for(TerminoZ termino: _zTerms)
			lhs = cplex.sum(lhs, cplex.prod(termino.coeficiente, _modelo.zVar(termino.hiperarista)));

		IloRange ret = null;
		
		if( _operador == Operador.LE )
			ret = cplex.le(lhs, _rhs);
		else if( _operador == Operador.GE )
			ret = cplex.ge(lhs, _rhs);
		else
			ret = cplex.eq(lhs, _rhs);
		
		return ret;
	}
	
	public boolean iguales(Desigualdad otra)
	{
		if( otra.xTerms().size() != this.xTerms().size() )
			return false;
		
		if( otra.zTerms().size() != this.zTerms().size() )
			return false;

		for(TerminoX termino: this.xTerms()) if( otra.contiene(termino.vertice, termino.color, termino.coeficiente) == false )
			return false;

		for(TerminoZ termino: this.zTerms()) if( otra.contiene(termino.hiperarista, termino.coeficiente) == false )
			return false;
		
		return true;
	}
	
	private boolean contiene(int vertice, int color, double coeficiente)
	{
		for(TerminoX termino: _xTerms)
		{
			if( termino.vertice == vertice && termino.color == color && termino.coeficiente == coeficiente )
				return true;
		}
		
		return false;
	}
	
	private boolean contiene(int hiperarista, double coeficiente)
	{
		for(TerminoZ termino: _zTerms)
		{
			if( termino.hiperarista == hiperarista && termino.coeficiente == coeficiente )
				return true;
		}
		
		return false;
	}
	
	private ArrayList<TerminoX> xTerms()
	{
		return _xTerms;
	}
	
	private ArrayList<TerminoZ> zTerms()
	{
		return _zTerms;
	}
	
	@Override public String toString()
	{
		String ret = "";
		
		for(TerminoX termino: _xTerms)
			ret += (termino.coeficiente > 0 ? " +" : " ") + termino.coeficiente + " x[" + termino.vertice + "," + termino.color + "]";
		
		for(TerminoZ termino: _zTerms)
			ret += (termino.coeficiente > 0 ? " +" : " ") + termino.coeficiente + " z[" + termino.hiperarista + "]";

		if( _operador == Operador.LE )
			ret += " <= " + _rhs;
		else if( _operador == Operador.GE )
			ret += " >= " + _rhs;
		else
			ret += " == " + _rhs;
		
		return ret + " [lhs = " + getLHS() + "]";
	}
}