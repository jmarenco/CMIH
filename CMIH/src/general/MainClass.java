package general;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ilog.concert.*;
import ilog.cplex.*;
import ilog.cplex.IloCplex.BooleanParam;
import ilog.cplex.IloCplex.DoubleParam;
import ilog.cplex.IloCplex.IntParam;
import ilog.cplex.IloCplex.Status;
 
public class MainClass
{
	// Niveles de log
	private static boolean _mostrarSolucion = false;
	private static boolean _verbose = false;
	private static boolean _resumen = true;
	
	// Tiempo anterior de cplex
	private static double _anterior = 0;
	
	// Formato con tres decimales
	private static DecimalFormat _format = new DecimalFormat("####0.000");
	
	// Especifica los parametros de una instancia de prueba
	private static class Parametros
	{
		public String archivo;
		public int pabellon;
		public int colores;
		public int azulesNuevas;
		public int semilla;
		
		public Parametros(String a, int p, int c, int an)
		{
			archivo = a;
			pabellon = p;
			colores = c;
			azulesNuevas = an;
			semilla = 0;
		}
	}
	
	// Instancias de prueba FCEyN
	private static List<Instancia> instanciasFCEyN() throws FileNotFoundException
	{
		Parametros[] parametros = new Parametros[]
			{
				new Parametros("instancias/2010-01.txt", 1, 21, 0), // [0]
				new Parametros("instancias/2010-01.txt", 2, 22, 0), // [1]
				new Parametros("instancias/2010-02.txt", 1, 18, 0), // [2]
				new Parametros("instancias/2010-02.txt", 2, 26, 0), // [3]
				new Parametros("instancias/2011-01.txt", 1, 16, 0), // [4]
				new Parametros("instancias/2011-01.txt", 2, 20, 0), // [5]
				new Parametros("instancias/2012-01.txt", 1, 18, 0), // [6]
				new Parametros("instancias/2012-01.txt", 2, 23, 0), // [7]
				new Parametros("instancias/2012-02.txt", 1, 20, 0), // [8]
				new Parametros("instancias/2012-02.txt", 2, 22, 0), // [9]
				new Parametros("instancias/2014-02.txt", 1, 20, 0), // [10]
				new Parametros("instancias/2014-02.txt", 2, 20, 0) // [11]
//				new Parametros("instancias/ungs.txt", 0, 90, 0)     // [12]
			};
		
		ArrayList<Instancia> instancias = new ArrayList<Instancia>(parametros.length);

		for(Parametros p: parametros)
			instancias.add(new Instancia(p.archivo, p.pabellon, p.colores).agregarHiperaristas(p.azulesNuevas, 0, 3, 5));
		
		return instancias;
	}
	
	// Instancias de prueba ITC 2007
	private static ArrayList<Instancia> instanciasITC() throws FileNotFoundException
	{
		// Cantidad de colores igual al numero cromatico para todas las instancias
		Parametros[] parametros = new Parametros[]
			{
				new Parametros("instancias/comp01-UD2.sol", 1, 8, 0),
				new Parametros("instancias/comp02-UD2.sol", 1, 17, 0),
				new Parametros("instancias/comp03-UD2.sol", 1, 16, 0),
				new Parametros("instancias/comp04-UD2.sol", 1, 16, 0),
				new Parametros("instancias/comp05-UD2.sol", 1, 9, 0),
				new Parametros("instancias/comp06-UD2.sol", 1, 19, 0),
				new Parametros("instancias/comp07-UD2.sol", 1, 22, 0),
				new Parametros("instancias/comp08-UD2.sol", 1, 20, 0),
				new Parametros("instancias/comp09-UD2.sol", 1, 15, 0),
				new Parametros("instancias/comp10-UD2.sol", 1, 21, 0),
				new Parametros("instancias/comp11-UD2.sol", 1, 8, 0),
				new Parametros("instancias/comp12-UD2.sol", 1, 11, 0),
				new Parametros("instancias/comp13-UD2.sol", 1, 18, 0),
				new Parametros("instancias/comp14-UD2.sol", 1, 18, 0),
				new Parametros("instancias/comp15-UD2.sol", 1, 14, 0),
				new Parametros("instancias/comp16-UD2.sol", 1, 21, 0),
				new Parametros("instancias/comp17-UD2.sol", 1, 17, 0),
				new Parametros("instancias/comp18-UD2.sol", 1, 9, 0),
				new Parametros("instancias/comp19-UD2.sol", 1, 15, 0),
				new Parametros("instancias/comp20-UD2.sol", 1, 20, 0),
				new Parametros("instancias/comp21-UD2.sol", 1, 20, 0)
			};
		
		ArrayList<Instancia> instancias = new ArrayList<Instancia>(parametros.length);

		for(Parametros p: parametros)
			instancias.add(new Instancia(p.archivo, p.pabellon, p.colores).agregarHiperaristas(p.azulesNuevas, 0, 3, 5));
		
		return instancias;
	}
	
	// Instancias de prueba agregando hiperaristas
	private static List<Instancia> instanciasAgregandoAzules() throws FileNotFoundException
	{
		ArrayList<Instancia> instancias = new ArrayList<Instancia>();

		for(int i=0; i<=50; i+=5)
		{
			Parametros p = new Parametros("instancias/2010-01.txt", 1, 21, i);
			Instancia instancia = new Instancia(p.archivo, p.pabellon, p.colores);

			instancia.agregarHiperaristas(p.azulesNuevas, 0, 3, 5);
			instancia.setNombre(instancia.getNombre() + " + " + i + "a");
			instancias.add(instancia);
		}
		
		return instancias;
	}
	
	// Procedimiento principal para resolver varias instancias
	public static void main(String[] args) throws NumberFormatException, FileNotFoundException
	{
//		List<Instancia> instancias = instanciasFCEyN();
//		List<Instancia> instancias = instanciasITC();
		List<Instancia> instancias = instanciasAgregandoAzules();
//		List<Instancia> instancias = new ArrayList<Instancia>(); instancias.add(instanciaDeEjemplo());
		
		for(Instancia instancia: instancias)
		{
			if( _verbose == true )
			{
				System.out.println();
				System.out.println("-------------------------------------------------------------");
				System.out.println();
				System.out.println(instancia.getNombre());
				System.out.println();
			}
			
			resolver(instancia);
		}
	}

	private static void resolver(Instancia instancia)
	{
		if( _verbose == true )
		{
			// Muestra datos de la instancia
			System.out.println(instancia.getVertices() + " vertices");
			System.out.println(instancia.cantidadAristas() + " aristas");
			System.out.println(instancia.cantidadHiperaristas() + " hiperaristas");
			System.out.println(instancia.getColores() + " colores");
			System.out.println();
		}

		// Genera y resuelve el modelo con cplex
		try
		{
			Modelo modelo = new Modelo(instancia);
			IloCplex cplex = modelo.crear();

			_anterior = cplex.getCplexTime();

			// Parametros
//			cplex.setParam(IntParam.Threads, 1); // Number of threads
			cplex.setParam(IloCplex.DoubleParam.TiLim, 600); // Time limit
//			cplex.setParam(DoubleParam.EpGap, 0.5); // Maximo gap relativo
			
//			cplex.setParam(BooleanParam.PreInd, false);
//			cplex.setParam(BooleanParam.ReverseInd, false);
//			cplex.setParam(IntParam.RelaxPreInd, 0);
//			cplex.setParam(IntParam.PreslvNd, -1);
//			cplex.setParam(IntParam.Reduce, 0);
			
//			cplex.setParam(IntParam.Cliques, -1);
//			cplex.setParam(IntParam.ZeroHalfCuts, -1);
//			cplex.setParam(IntParam.FracCuts, -1);
//			cplex.setParam(IntParam.MIRCuts, -1);
//			cplex.setParam(IntParam.HeurFreq, -1);
//			cplex.setParam(IntParam.NodeLim, 1);
			cplex.setWarning(null);
			
			if( _verbose == false )
				cplex.setParam(IntParam.MIPDisplay, 0);
			
			cplex.use(new Separador(modelo));

			// optimize and output solution information
			if( cplex.solve() )
			{
				if( _mostrarSolucion && (cplex.getStatus() == Status.Optimal || cplex.getStatus() == Status.Feasible) )
				{
					System.out.println("Solution status = " + cplex.getStatus());
					System.out.println("Solution value  = " + cplex.getObjValue());
					System.out.println("Solution time   = " + (cplex.getCplexTime() - _anterior));
					System.out.println("Relative gap    = " + cplex.getMIPRelativeGap());
					System.out.println("Nodes           = " + cplex.getNnodes());
					System.out.println();
					
					for(int i=0; i<instancia.getVertices(); ++i)
					for(int j=0; j<instancia.getColores(); ++j) if( cplex.getValue(modelo.xVar(i, j)) > 0.1 )
						System.out.println("x[" + i + ", " + j + "] = " + cplex.getValue(modelo.xVar(i, j)));
				
					System.out.println();
					
					for(int h=0; h<instancia.cantidadHiperaristas(); ++h)
						System.out.println("z[" + h + "] = " + cplex.getValue(modelo.zVar(h)));

					System.out.println();
				}
			}

			if( _resumen == true )
			{
				System.out.print(instancia.getNombre() + " | ");
				System.out.print("n: " + instancia.getVertices() + " | ");
				System.out.print("mG: " + instancia.cantidadAristas() + " | ");
				System.out.print("mH: " + instancia.cantidadHiperaristas() + " | ");
				System.out.print("c: " + instancia.getColores() + " | ");
				System.out.print(cplex.getStatus() + " | ");
				System.out.print(_format.format(cplex.getCplexTime() - _anterior) + " sg | ");
				System.out.print(cplex.getStatus() != IloCplex.Status.Infeasible ? "Obj: " + (int)cplex.getObjValue() + " | " : "--- | ");
				System.out.print(cplex.getStatus() != IloCplex.Status.Infeasible ? cplex.getMIPRelativeGap() + "% | " : "--- | ");
				System.out.print("Nod: " + cplex.getNnodes() + " | ");

				SeparadorPartitioned.mostrarResumen();
				System.out.println();
			}
			
			_anterior = cplex.getCplexTime();
			cplex.end();
		}
		catch (IloException e)
		{
			System.err.println("Concert exception caught: " + e);
		}
	}

	// Instancia de ejemplo
	private static Instancia instanciaDeEjemplo()
	{
		Instancia instancia = new Instancia(7);
		instancia.setColores(3);
		instancia.setNombre("Test");
		
		instancia.setArista(0, 1);
		instancia.setArista(0, 2);
		instancia.setArista(0, 4);
		instancia.setArista(1, 3);
		instancia.setArista(1, 4);
		instancia.setArista(2, 6);
		instancia.setArista(3, 5);
		instancia.setArista(4, 5);
		instancia.setArista(4, 6);
		instancia.setArista(5, 6);
		instancia.setHiperarista(0, 5);
		instancia.setHiperarista(2, 3, 4);
		instancia.setHiperarista(1, 6);
		instancia.setHiperarista(1, 2, 3, 5);
		
		return instancia;
	}
}