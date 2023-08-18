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
	
	public int size()
	{
		return _vertices.size();
	}
	
	public int pares()
	{
		return (size() * (size()-1)) / 2;
	}
}
