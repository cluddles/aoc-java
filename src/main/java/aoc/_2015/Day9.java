package aoc._2015;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import shared.ResourceUtil;

public class Day9 {

	static class Place {
		final String name;
		final List<Connection> connections = new ArrayList<>();
		Place(String name) {
			this.name = name;
		}
	}

	static class Connection {
		final Place place;
		final int dist;
		Connection(Place place, int dist) {
			this.place = place;
			this.dist = dist;
		}
	}

	public void solve(String path) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(path);

		// Read places, connections from file
		Map<String, Place> places = new HashMap<>();
		for (String line : lines) {
			String[] split = line.split(" ");
			connect(places, split[0], split[2], Integer.parseInt(split[4]));
		}

		// Brute force
		int best = visit(places);
		System.out.println(best);
	}

	private void connect(Map<String, Place> places, String from, String to, int dist) {
		Place placeFrom = places.get(from);
		if (placeFrom == null) {
			placeFrom = new Place(from);
			places.put(from, placeFrom);
		}
		Place placeTo = places.get(to);
		if (placeTo == null) {
			placeTo = new Place(to);
			places.put(to, placeTo);
		}
		placeFrom.connections.add(new Connection(placeTo, dist));
		placeTo.connections.add(new Connection(placeFrom, dist));
	}

	private int best(int previousBest, int score) {
		// if (previousBest == -1 || score < previousBest) return score; // p1
		if (previousBest == -1 || score > previousBest) return score; // p2
		return previousBest;
	}

	private int visit(Map<String, Place> places) {
		int best = -1;
		for (Place place : places.values()) {
			best = best(best, traverse(place, new HashSet<>()));
		}
		return best;
	}

	private int traverse(Place current, Set<Place> visited) {
		int best = -1;
		visited.add(current);
		for (Connection connection : current.connections) {
			if (visited.contains(connection.place)) continue;
			best = best(best, traverse(connection.place, new HashSet<>(visited)) + connection.dist);
		}
		return Math.max(best, 0);
	}

	public static void main(String[] args) throws Exception {
		Day9 worker = new Day9();
		worker.solve("2015/day9.input");
	}

}
