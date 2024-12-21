package aoc._2016;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import shared.ResourceUtil;

public class Day20 {

	public void from(String name) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(name);

		// p1
		RangeSet<Long> ranges = TreeRangeSet.create();
		for (String line : lines) {
			String[] split = line.split("-");
			ranges.add(Range.closed(
					Long.parseLong(split[0]),
					Long.parseLong(split[1])));
		}
		System.out.println(ranges);

		// p2
		long permittedValues = 4294967296L;
		for (Range<Long> range : ranges.asRanges()) {
			permittedValues -= (range.upperEndpoint() - range.lowerEndpoint() + 1);
		}
		System.out.println(permittedValues);
	}

	public static void main(String[] args) throws Exception {
		Day20 worker = new Day20();
		worker.from("2016/day20.input");
	}

}
