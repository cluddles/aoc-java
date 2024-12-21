package aoc._2017;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import shared.ResourceUtil;
import shared.Test;

public class Day10 {

	class Result {
		final List<Integer> elements;
		final int pos;
		final int skip;
		Result(List<Integer> elements, int pos, int skip) {
			this.elements = elements;
			this.pos = pos;
			this.skip = skip;
		}
	}

	public int get(List<Integer> elements, int pos) {
		return elements.get(pos % elements.size());
	}
	public void set(List<Integer> elements, int pos, int value) {
		elements.set(pos % elements.size(), value);
	}

	public void flipSection(List<Integer> elements, int start, int end) {
		int size = end - start;
		for (int i = 0; i < size / 2; i++) {
			int temp = get(elements,start + i);
			set(elements, start + i, get(elements, end - i - 1));
			set(elements, end - i - 1, temp);
		}
	}

	public Result oneRound(
			List<Integer> elements,
			List<Integer> lengths,
			int pos,
			int skip) {
		for (int length : lengths) {
			flipSection(elements, pos, pos + length);
			pos = pos + length + skip;
			skip++;
		}
		return new Result(elements, pos, skip);
	}

	public List<Integer> partOne(int numElements, String input) {
		List<Integer> elements = generateElements(numElements);
		List<Integer> lengths = simpleInput(input);
		return oneRound(elements, lengths, 0, 0).elements;
	}

	public List<Integer> generateElements(int numElements) {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < numElements; i++) {
			result.add(i);
		}
		return result;
	}

	public List<Integer> simpleInput(String input) {
		return Arrays.stream(input.split(","))
				.map(Integer::valueOf)
				.collect(Collectors.toList());
	}

	public String hash(String input) {
		// Input with standard trailer
		input = input + (char)17 + (char)31 + (char)73 + (char)47 + (char)23;
		List<Integer> lengths = input.chars()
				.mapToObj(i -> i)
				.collect(Collectors.toList());
		// 64 rounds
		Result result = new Result(generateElements(256), 0, 0);
		for (int i = 0; i < 64; i++) {
			result = oneRound(result.elements, lengths, result.pos, result.skip);
		}
		// Sparse hash to dense hash
		String hash = "";
		int pos = 0;
		for (int i = 0; i < 16; i++) {
			int current = result.elements.get(pos);
			pos++;
			for (int j = 1; j < 16; j++) {
				current = current ^ result.elements.get(pos);
				pos++;
			}
			// Make sure it's two digits!
			String hex = Integer.toHexString(current);
			if (hex.length() == 1) hex = "0" + hex;
			hash = hash + hex;
		}
		return hash;
	}

	public void examples() {
		List<Integer> result = partOne(5, "3,4,1,5");
		Test.check(result.get(0), 3);
		Test.check(result.get(1), 4);

		//Test.check(hash(""), "a2582a3a0e66e6e86e3812dcb672a272");
		Test.check(hash("AoC 2017"), "33efeb34ea91902bb2f59c9920caa6cd");
		Test.check(hash("1,2,3"), "3efbe78a8d82f29979031a4aa0b16a9d");
		Test.check(hash("1,2,4"), "63960835bcdc130f0b66d7ff4f6a5a8e");
	}

	public static void main(String[] args) throws Exception {
		Day10 day = new Day10();
		day.examples();

		String input = ResourceUtil.readString("2017/day10.input");
		List<Integer> result = day.partOne(256, input);
		System.out.println(result);
		System.out.println(result.get(0) * result.get(1));

		System.out.println(day.hash(input));
	}

}
