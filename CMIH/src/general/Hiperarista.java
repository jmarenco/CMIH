package general;

import java.util.ArrayList;

public class Hiperarista
{
	ArrayList<Integer> _vertices;
	
	public Hiperarista()
	{
		_vertices = new ArrayList<Integer>();
	}
	
	public void agregar(int vertice)
	{
		if( _vertices.contains(vertice) == false )
			_vertices.add(vertice);
	}
	
	public ArrayList<Integer> getVertices()
	{
		return _vertices;
	}
	
	public boolean contiene(int vertice)
	{
		return _vertices.contains(vertice);
	}
	
	public int get(int i)
	{
		return _vertices.get(i);
	}
	
	public int size()
	{
		return _vertices.size();
	}
	
	public int pares()
	{
		return (size() * (size()-1)) / 2;
	}
	
	public boolean vecino(int vertice, Instancia instancia)
	{
		return _vertices.stream().anyMatch(v -> instancia.getArista(vertice, v));
	}
	
	public boolean vecina(Hiperarista otra, Instancia instancia)
	{
		for(Integer i: otra.getVertices())
		for(Integer j: this.getVertices()) if( instancia.getArista(i, j) )
			return true;
		
		return false;
	}
}
