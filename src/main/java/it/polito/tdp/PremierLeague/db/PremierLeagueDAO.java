package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Arco;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Matches giocati nel mese selezionato
	 * @param idMap
	 */
	public void loadAllVertices(Map<Integer, Match> idMap, Integer mese) {
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID AND Month(m.date)=?";
	
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1,mese);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(!idMap.containsKey(res.getInt("m.MatchID"))) {
					Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
					idMap.put(res.getInt("m.MatchID"), match);

				}
				conn.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return ;
		}
		
	}
	
	/**
	 * Creo archi in cui ho due match diversi come vertici
	 * con peso = #giocatori che hanno giocato almeno MIN minuti
	 * in tutti e due i match
	 * (peso>0)
	 * @param idMap
	 */
	public List <Arco> getArchi (Integer MIN,Map<Integer, Match> idMap) {
		String sql = "SELECT m1.MatchID as id1, m2.MatchID as id2, COUNT(DISTINCT a1.PlayerID) as peso "
				+ "FROM Matches as m1, Matches as m2, Actions as a1, Actions as a2 "
				+ "WHERE m1.MatchID=a1.MatchID and m2.MatchID=a2.MatchID and  m1.MatchID> m2.MatchID and "
				+ "a1.PlayerID=a2.PlayerID and a1.TimePlayed>=? and a2.TimePlayed>=? "
				+ "GROUP BY m1.MatchID , m2.MatchID ";
	
		Connection conn = DBConnect.getConnection();
		List <Arco> archi = new ArrayList <Arco>();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1,MIN);
			st.setInt(2,MIN);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				//System.out.println(idMap.get("id1")+" - "+idMap.get("id2")+" peso: "+res.getInt("peso"));
				Match m1 = idMap.get(res.getInt("id1"));
				Match m2 = idMap.get(res.getInt("id2"));
				
				if(m1!=null && m2!=null && res.getInt("peso")>0) {
					Arco a = new Arco(m1,m2,res.getInt("peso"));
					archi.add(a);
				}
			
			}
			conn.close();
			return archi;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
}
