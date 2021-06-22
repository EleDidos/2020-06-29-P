package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private SimpleWeightedGraph< Match , DefaultWeightedEdge>graph;
	private Map <Integer,  Match > idMap;
	private PremierLeagueDAO dao;
	private List <Match> best;
	
	private Integer mese;
	private Integer MIN;
	
	
	public Model() {
		idMap= new HashMap <Integer, Match >();
		dao=new PremierLeagueDAO();
	}
	
	public void creaGrafo(Integer mese, Integer MIN) {
		graph= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.mese=mese;
		this.MIN=MIN;
		
		dao.loadAllVertici(idMap, mese);
		Graphs.addAllVertices(graph, idMap.values());
		
		for(Arco a : dao.listArchi(idMap,MIN,mese))
			Graphs.addEdge(graph,a.getM1(),a.getM2(),a.getPeso());
		
		
	}
	
	public Integer getNVertici() {
		return graph.vertexSet().size();
	}
	
	public Integer getNArchi() {
		return graph.edgeSet().size();
	}
	
	
	public String getCoppieTOP(){
		String coppieTOP="";
		double max=0.0;
		
		//trovo max
		for(DefaultWeightedEdge e: graph.edgeSet()) {
			if(graph.getEdgeWeight(e)>max)
				max=graph.getEdgeWeight(e);
		}
		//coppie con max di min
		for(DefaultWeightedEdge e: graph.edgeSet()) {
			if(graph.getEdgeWeight(e)==max)
				coppieTOP+=graph.getEdgeSource(e)+" ---> " +graph.getEdgeTarget(e)+"\n";
		}
		return coppieTOP;
	}
	
	public List <Match> getVertici(){
		List <Match> vertici = new ArrayList <Match>();
		for( Match m    : graph.vertexSet())
			vertici.add( m );
		Collections.sort(vertici);
		return vertici;
	}

	public SimpleWeightedGraph< Match , DefaultWeightedEdge> getGraph() {
		return graph;
	}
	
	private Match partenza;
	private Match arrivo;
	private double pesoMax=0.0;
	
	public List <Match> trovaPercorso(Match partenza, Match arrivo){
		this.partenza=partenza;
		this.arrivo=arrivo;
		best=new ArrayList <Match>();
		
		List <Match> parziale = new ArrayList <Match>();
		parziale.add(partenza);
		cerca(parziale);
		return best;
	}
	
	
	private void cerca(List <Match> parziale) {
		if(parziale.get(parziale.size()-1).equals(arrivo)) {
			if(best.size()==0 || calcolaPeso(parziale)>pesoMax) {
				best=new ArrayList <Match> (parziale);
				pesoMax=calcolaPeso(parziale);
				return;
			}
		}
		
		for(Match m: Graphs.neighborListOf(graph, parziale.get(parziale.size()-1))) {
			//controllo che il nuovo match che vado ad aggiungere
			//non coinvolga le stesse squadre del match precedente
			if(!parziale.contains(m) & this.differentTeams(parziale.get(parziale.size()-1), m)==true) {
				parziale.add(m);
				cerca(parziale);
				parziale.remove(parziale.size()-1);
			}
		}
		
	}
	
	
	private Double calcolaPeso(List <Match> lista) {
		double peso=0.0;
		for(int i=0;i<lista.size()-1;i++) {
			Match m1=lista.get(i);
			Match m2=lista.get(i+1);
			DefaultWeightedEdge e=graph.getEdge(m1, m2);
			peso+=graph.getEdgeWeight(e);
		}
		return peso;
	}
	
	
	private boolean differentTeams(Match m1, Match m2) {
		if( ( m1.getTeamHomeID()==m2.getTeamAwayID() ) & ( m1.getTeamAwayID()==m2.getTeamHomeID() ))
			return false;
		return true;
	}
}
