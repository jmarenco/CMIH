package general;

import java.util.ArrayList;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

public class Separador extends IloCplex.UserCutCallback
{
	private IloCplex _cplex;
	private Modelo _modelo;
	private ArrayList<SeparadorGenerico> _separadores;
	
	private static boolean _active = true;
	private static boolean _cutAndBranch = false;
	private static int _maxRounds = 10;
	private static int _skipFactor = 0;
	
	private IloCplex.NodeId _root;
	private IloCplex.NodeId _lastNode;
	private int _rounds;
	private int _skipped;
	private int _executions = 0;
	private double _tiempo = 0;
	
	public Separador(Modelo modelo)
	{
		SeparadorPartitioned.inicializarEstadisticas();
		SeparadorGenPartitioned.inicializarEstadisticas();
		SeparadorUnionSimple.inicializarEstadisticas();
		SeparadorDosClique.inicializarEstadisticas();
		SeparadorTresClique.inicializarEstadisticas();

		_cplex = modelo.getCplex();
		_modelo = modelo;
		_separadores = new ArrayList<SeparadorGenerico>();
		
		_separadores.add( new SeparadorPartitioned(this) );
		_separadores.add( new SeparadorGenPartitioned(this) );
		_separadores.add( new SeparadorUnionSimple(this) );
		_separadores.add( new SeparadorDosClique(this) );
		_separadores.add( new SeparadorTresClique(this) );
	}
	
	@Override
	protected void main() throws IloException
	{
		if( this.isAfterCutLoop() == false || _active == false )
	        return;
		
		IloCplex.NodeId current = updateNodes();

		if( _cutAndBranch && current.equals(_root) == false )
			return;
		
		if( _rounds > _maxRounds )
			return;
		
		if (_skipped < _skipFactor )
			return;

		double inicio = _cplex.getCplexTime();
		
		Solucion solucion = new Solucion(this);
		for(SeparadorGenerico separador: _separadores)
			separador.run(solucion);
		
		_tiempo += _cplex.getCplexTime() - inicio;
		_skipped = 0;
		_executions += 1;
	}
	
	private IloCplex.NodeId updateNodes() throws IloException
	{
		IloCplex.NodeId current = this.getNodeId();
		
		if( current == null )
			return null;
		
		if( _root == null )
		{
			_root = current;
			_skipped = _skipFactor; // Cuts in the first node
		}
		
		if( current.equals(_lastNode) == false )
		{
			_lastNode = current;
			_rounds = 0;
			_skipped += 1;
		}
		
		_rounds++;
		
		return current;
	}
	
	public void agregar(IloRange cut) throws IloException
	{
		this.add(cut, IloCplex.CutManagement.UseCutForce);
	}
	
	public static void setActive(boolean active)
	{
		_active = active;
	}
	
	public static void setCutAndBranch(boolean cutAndBranch)
	{
		_cutAndBranch = cutAndBranch;
	}
	
	public static void setMaxRounds(int maxRounds)
	{
		_maxRounds = maxRounds;
	}
	
	public static void setSkipFactor(int skipFactor)
	{
		_skipFactor = skipFactor;
	}
	
	public static boolean getCutAndBranch()
	{
		return _cutAndBranch;
	}

	public static int getMaxRounds()
	{
		return _maxRounds;
	}
	
	public static int getSkipFactor()
	{
		return _skipFactor;
	}

	public int getExecutions()
	{
		return _executions;
	}
	
	public double getTiempo()
	{
		return _tiempo;
	}
	
	public Modelo getModelo()
	{
		return _modelo;
	}
	
	public double getValor(IloNumVar var) throws IloException
	{
		return this.getValue(var);
	}
}