package general;

import java.util.ArrayList;

import ilog.concert.IloException;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

public class Separador extends IloCplex.UserCutCallback
{
	private IloCplex _cplex;
	private Modelo _modelo;
	private ArrayList<SeparadorGenerico> _separadores;
	
	private int _ejecuciones;
	private static double _tiempo;
	
	public Separador(Modelo modelo)
	{
		_cplex = modelo.getCplex();
		_modelo = modelo;
		_separadores = new ArrayList<SeparadorGenerico>();
		_ejecuciones = 0;
		_tiempo = 0;
		
//		_separadores.add( new SeparadorPartitioned(this) );
//		_separadores.add( new SeparadorGenPartitioned(this) );
//		_separadores.add( new SeparadorUnionSimple(this) );
	}
	
	@Override
	protected void main() throws IloException
	{
		if( !this.isAfterCutLoop() )
	        return;
		
		if( _ejecuciones > 0 )
			return;
		
		++_ejecuciones;
		
//		if( (_ejecuciones++) % 10 != 0 )
//			return;

		double inicio = _cplex.getCplexTime();
		
		Solucion solucion = new Solucion(_modelo);
		for(SeparadorGenerico separador: _separadores)
			separador.run(solucion);
		
		_tiempo += _cplex.getCplexTime() - inicio;
	}
	
	public void agregar(IloRange cut) throws IloException
	{
		this.add(cut, IloCplex.CutManagement.UseCutForce);
	}
	
	public static double tiempo()
	{
		return _tiempo;
	}
	
	public Modelo getModelo()
	{
		return _modelo;
	}
}