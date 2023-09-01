package general;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Instancia
{
	// Archivo
	private String _archivo;
	
	// Cantidad de vertices y colores
	private int _n;
	private int _c;
	
	// Aristas del grafo G
	private boolean[][] _aristas;
	
	// Aristas del grafo H
	private ArrayList<Hiperarista> _hiperaristas;
	
	// Constructor para generar una instancia por codigo
	public Instancia(int n)
	{
		_n = n;
		_c = n;
		_aristas = new boolean[n][n];
		_hiperaristas = new ArrayList<Hiperarista>();
	}

	// Auxiliar para la lectura de archivos
	private static class Clase
	{
		public String nombre;
		public String docente;
		public String dia;
		public double horaInicio;
		public double horaFin;
		public int pabellon;
		
		public enum Tipo { FCEN, ITC };
		
		public Clase(String linea, Tipo tipo)
		{
			if( tipo == Tipo.FCEN )
			{
				String[] campos = linea.split("\\|");
				
				if( campos.length < 6 )
					throw new RuntimeException("Error de formato! Linea: " + linea);
				
				nombre = campos[0].trim();
				docente = campos[1].trim();
				dia = campos[2].trim();
				horaInicio = Double.parseDouble(campos[3].trim());
				horaFin = Double.parseDouble(campos[4].trim());
				pabellon = Integer.parseInt(campos[5].trim());
			}
			
			if( tipo == Tipo.ITC )
			{
				String[] campos = linea.split(" ");
				
				if( campos.length < 4 )
					throw new RuntimeException("Error de formato! Linea: " + linea);
				
				nombre = campos[0].trim();
				docente = "";
				dia = campos[2].trim();
				horaInicio = Double.parseDouble(campos[3].trim());
				horaFin = horaInicio + 1;
				pabellon = 1;
			}
		}
		
		public static boolean seSuperponen(Clase primera, Clase segunda)
		{
			return primera.dia.equals(segunda.dia) && !(primera.horaFin <= segunda.horaInicio || primera.horaInicio >= segunda.horaFin);
		}
		
		public static boolean igualMateria(Clase primera, Clase segunda)
		{
			String clavePrimera = primera.nombre + "|" + primera.docente;
			String claveSegunda = segunda.nombre + "|" + segunda.docente;
			
			return clavePrimera.equals(claveSegunda);
		}
	}
	
	// Constructor para leer una instancia desde un archivo (instancias FCEyN)
	public Instancia(String archivo, int pabellon, int colores) throws FileNotFoundException
	{
		_archivo = archivo + " (P" + pabellon + ")";
		
		FileInputStream fis = new FileInputStream(archivo);
		Scanner in = new Scanner(fis);
		
		ArrayList<Clase> clases = new ArrayList<Clase>();
		while( in.hasNextLine() )
		{
			Clase clase = null;
			try
			{
				clase = new Clase(in.nextLine(), Clase.Tipo.FCEN);
			}
			catch(Exception e)
			{
			}
			
			if( clase != null && (pabellon == 0 || clase.pabellon == pabellon) )
				clases.add(clase);
		}
		
		in.close();
		
//		for(int i=0; i<clases.size(); ++i)
//			System.out.println(i + "|" + clases.get(i).nombre + "|" + clases.get(i).docente + "|" + clases.get(i).dia + "|" + clases.get(i).horaInicio + "|" + clases.get(i).horaFin + "|" + clases.get(i).pabellon);

		_n = clases.size();
		_c = colores;
		_aristas = new boolean[_n][_n];
		_hiperaristas = new ArrayList<Hiperarista>();
		
		for(int i=0; i<clases.size(); ++i)
		for(int j=i+1; j<clases.size(); ++j)
		{
			if( Clase.seSuperponen(clases.get(i), clases.get(j)) )
				setArista(i, j);
		}

		for(int i=0; i<clases.size(); ++i)
		{
			Hiperarista hiperarista = new Hiperarista();
			hiperarista.agregar(i);
			
			for(int j=i+1; j<clases.size(); ++j)
			{
				if( !Clase.seSuperponen(clases.get(i), clases.get(j)) && Clase.igualMateria(clases.get(i), clases.get(j)) )
					hiperarista.agregar(j);
			}
			
			if( hiperarista.size() > 1 )
				agregar(hiperarista);
		}
	}

	// Constructor para leer una instancia desde un archivo (instancias ITC-2007)
	public Instancia(String archivo, int colores) throws FileNotFoundException
	{
		_archivo = archivo;
		
		FileInputStream fis = new FileInputStream(archivo);
		Scanner in = new Scanner(fis);
		Clase anterior = null;
		
		ArrayList<Clase> clases = new ArrayList<Clase>();
		while( in.hasNextLine() )
		{
			Clase clase = null;
			try
			{
				clase = new Clase(in.nextLine(), Clase.Tipo.ITC);
			}
			catch(Exception e)
			{
			}
			
			if( anterior != null && Clase.igualMateria(clase, anterior) && anterior.dia.equals(clase.dia) )
			{
				anterior.horaInicio = Math.min(anterior.horaInicio, clase.horaInicio);
				anterior.horaFin = Math.max(anterior.horaFin, clase.horaFin);
			}
			else
			{
				clases.add(clase);
				anterior = clase;
			}
		}
		
		in.close();

//		for(int i=0; i<clases.size(); ++i)
//			System.out.println(i + "|" + clases.get(i).nombre + "|" + clases.get(i).docente + "|" + clases.get(i).dia + "|" + clases.get(i).horaInicio + "|" + clases.get(i).horaFin + "|" + clases.get(i).pabellon);

		_n = clases.size();
		_c = colores;
		_aristas = new boolean[_n][_n];
		_hiperaristas = new ArrayList<Hiperarista>();
		
		for(int i=0; i<clases.size(); ++i)
		for(int j=i+1; j<clases.size(); ++j)
		{
			if( Clase.seSuperponen(clases.get(i), clases.get(j)) )
				setArista(i, j);
		}

		for(int i=0; i<clases.size(); ++i)
		{
			Hiperarista hiperarista = new Hiperarista();
			hiperarista.agregar(i);
			
			for(int j=i+1; j<clases.size(); ++j)
			{
				if( !Clase.seSuperponen(clases.get(i), clases.get(j)) && Clase.igualMateria(clases.get(i), clases.get(j)) )
					hiperarista.agregar(j);
			}
			
			if( hiperarista.size() > 1 )
				agregar(hiperarista);
		}
	}
	
	// Agregado aleatorio de aristas
	public Instancia agregarAristas(int cantidad, int seed)
	{
		Random random = new Random(seed);
		
		for(int i=0; i<cantidad; ++i)
		{
			int j = random.nextInt(this.getVertices());
			int k = random.nextInt(this.getVertices());
			
			if( j != k )
				this.setArista(j, k);
		}
		
		return this;
	}

	// Agregado aleatorio de hiperaristas
	public Instancia agregarHiperaristas(int cantidad, int seed, int minSize, int maxSize)
	{
		Random random = new Random(seed);
		
		for(int i=0; i<cantidad; ++i)
		{
			Hiperarista hiperarista = new Hiperarista();
			hiperarista.agregar(random.nextInt(this.getVertices()));

			int tamano = minSize + random.nextInt(maxSize-minSize+1);
			for(int j=1; j<tamano; ++j)
			{
				int k = random.nextInt(this.getVertices());
				while( hiperarista.contiene(k) || hiperarista.vecino(k, this) )
					k = random.nextInt(this.getVertices());
				
				hiperarista.agregar(k);
			}
			
			this.agregar(hiperarista);
		}
		
		return this;
	}
	
	// Agregado de aristas
	public void setArista(int i, int j)
	{
		if( i < 0 || i >= _n || j < 0 || j >= _n )
			throw new IllegalArgumentException();
		
		if( i == j )
			return;
		
		_aristas[i][j] = true;
		_aristas[j][i] = true;
	}
	
	// Consulta de aristas
	public boolean getArista(int i, int j)
	{
		if( i < 0 || i >= _n || j < 0 || j >= _n )
			throw new IllegalArgumentException("Arista.getArista(), i = " + i + ", j = " + j);
		
		return _aristas[i][j];
	}
	
	// Agregado de hiperaristas
	public void agregar(Hiperarista hiperarista)
	{
		_hiperaristas.add(hiperarista);
	}
	
	public void setHiperarista(int... vertices)
	{
		Hiperarista nueva = new Hiperarista();
		for(int i=0; i<vertices.length; ++i)
			nueva.agregar(vertices[i]);
		
		agregar(nueva);
	}
	
	// Consulta de hiperaristas
	public ArrayList<Hiperarista> getHiperaristas()
	{
		return _hiperaristas;
	}
	
	public Hiperarista getHiperarista(int h)
	{
		return _hiperaristas.get(h);
	}
	
	// Cantidad de vertices y colores
	public int getVertices()
	{
		return _n;
	}
	public int getColores()
	{
		return _c;
	}
	public void setColores(int valor)
	{
		_c = valor;
	}
	
	public static class Par
	{
		public int vertice1;
		public int vertice2;
		
		public Par(int v1, int v2)
		{
			vertice1 = v1;
			vertice2 = v2;
		}
	}
	
	public int cantidadAristas()
	{
		int ret = 0;
		
		for(int i=0; i<_n; ++i)
		for(int j=i+1; j<_n; ++j) if( _aristas[i][j] == true )
			++ret;
		
		return ret;
	}
	
	public int cantidadHiperaristas()
	{
		return _hiperaristas.size();
	}
	
	public int paresTotales()
	{
		return _hiperaristas.stream().mapToInt(h -> h.pares()).sum();
	}

	// Nombre de la instancia
	public void setNombre(String valor)
	{
		_archivo = valor;
	}
	public String getNombre()
	{
		return _archivo;
	}
}