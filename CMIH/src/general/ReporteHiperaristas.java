package general;

public class ReporteHiperaristas
{
	public static void mostrar(Instancia instancia)
	{
		int[] tamanos = new int[instancia.getVertices()];
		int[] invalidas = new int[instancia.getVertices()];
		
		for(Hiperarista hiperarista: instancia.getHiperaristas())
		{
			tamanos[hiperarista.size()] += 1;
			
			boolean aristas = false;
			
			for(int i=0; i<hiperarista.size(); ++i)
			for(int j=i+1; j<hiperarista.size(); ++j)
			{
				if( instancia.getArista(hiperarista.get(i), hiperarista.get(j)) )
					aristas = true;
			}
			
			if( aristas == true )
				invalidas[hiperarista.size()] += 1;
		}
		
		for(int i=0; i<instancia.getVertices(); ++i) if( tamanos[i] > 0 )
		{
			System.out.print("|H| = " + i + ": " + tamanos[i] + " hiperaristas");
			
			if( invalidas[i] > 0 )
				System.out.print(" (" + invalidas[i] + " invalidas)");
			
			System.out.println();
		}
	}
}
