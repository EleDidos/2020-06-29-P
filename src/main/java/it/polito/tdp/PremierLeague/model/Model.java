package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private SimpleWeightedGraph <Match, DefaultWeightedEdge> graph;
	private Map <Integer, Match> idMap;
	private PremierLeagueDAO dao;
	
	private int mese;
	private int MIN;
	
	private List <Arco> best;
	private double pesoMax;
	
	public Model() {
		idMap = new HashMap <Integer, Match>();
		dao = new PremierLeagueDAO();
	}

	public void creaGrafo(Integer mese, Integer MIN) {
		this.mese=mese;
		this.MIN=MIN;
		graph = new SimpleWeightedGraph <>(DefaultWeightedEdge.class);
		
		dao.loadAllVertices(idMap, mese);
		Graphs.addAllVertices(graph, idMap.values());
		//stampa vertici
		for(Match m: graph.vertexSet())
			System.out.print(m.getMatchID()+"; ");
		
		System.out.println("\n");
		
		for(Arco a: dao.getArchi(MIN, idMap)) {
			Graphs.addEdge(graph, a.getM1(), a.getM2(), a.getPeso());
			System.out.println(a.toString());
		}
		
	}
	
	public int getNVertici() {
		return graph.vertexSet().size();
		
	}
	
	public int getNArchi() {
		return graph.edgeSet().size();
	}

	public SimpleWeightedGraph<Match, DefaultWeightedEdge> getGraph() {
		return graph;
	}

	
	public List <Arco> getArchiMax() {
		List <Arco> archiMax=new ArrayList <Arco>();
		
		//trova peso max
		double max=0;
		for(DefaultWeightedEdge e: graph.edgeSet())
			if(graph.getEdgeWeight(e)>max)
				max=graph.getEdgeWeight(e);
		//elenca tutti quelli con peso max
		for(DefaultWeightedEdge e: graph.edgeSet())
			if(graph.getEdgeWeight(e)==max)
				archiMax.add(new Arco ( graph.getEdgeSource(e), graph.getEdgeTarget(e), (int)graph.getEdgeWeight(e) ));
		
		return archiMax;
	}
	
	public Collection <Match> getVertici(){
		return graph.vertexSet();
	}

	
	//RICORSIONE
	
	public List<Arco> trovaPercorso(Match m1, Match m2) {
		best= new ArrayList <Arco>();
		pesoMax=0.0;
		List <Arco> parziale = new ArrayList <Arco>();
		
		cerca(m1, m2, parziale);
		
		return best;
	}

	
	private void cerca(Match partenza, Match destinazione, List<Arco> parziale) {
		//caso terminale: ultimo arco aggiunto ha come vertice target la destinazione
		if( parziale.size()>0)
			if( parziale.get (parziale.size()-1).getM2().equals(destinazione) ) {
			
			if(this.getPesoLista(parziale)>pesoMax) {
				pesoMax=this.getPesoLista(parziale);
				best=new ArrayList <Arco>(parziale);
			}
				return;
			
		}//caso terminale
		
		//RICORSIONE
		//per ogni arco uscente da partenza
		for(DefaultWeightedEdge e: graph.outgoingEdgesOf(partenza)) {
			
			/** PARTENZA rimane in target o in source???? 
			Arco prova = new Arco ( graph.getEdgeSource(e), graph.getEdgeTarget(e), (int)graph.getEdgeWeight(e) );
			**/
			
			//M1NEW= partenza
			//cerco di capire se m2NEW, il vertice diverso da partenza
			//è la source o il target
			Match m1NEW=partenza;
			Match m2NEW;
			if(graph.getEdgeSource(e).equals(partenza))
				m2NEW=graph.getEdgeTarget(e);
			else
				m2NEW=graph.getEdgeSource(e);
			Arco prova = new Arco (m1NEW,m2NEW,(int)graph.getEdgeWeight(e) );
			
			if(!parziale.contains(prova) )
				//se match di partenza e quello di arrivo dell'arco sono giocati da squadre diverse
				if(!this.sameTeams(prova.getM1(),prova.getM2())) {
					parziale.add(prova);
					cerca(prova.getM2(), destinazione, parziale);
					//backtracking
					parziale.remove(parziale.size()-1);
				}//2° if
					
		}//for
		
			
		
	}
	
	
	private double getPesoLista(List <Arco> lista) {
		double peso=0.0;
		for(Arco a: lista)
			peso+=a.getPeso();
		return peso;
	}
	
	
	/**
	 * controlla se due match sono stati giocati dalle stesse squadre
	 */
	private boolean sameTeams(Match m1, Match m2) {
		if( (m1.getTeamHomeID()==m2.getTeamHomeID() && m1.getTeamAwayID()==m2.getTeamAwayID())
				|| (m1.getTeamHomeID()==m2.getTeamAwayID() && m1.getTeamAwayID()==m2.getTeamHomeID()) )
			return true;
		return false;
	}
}
