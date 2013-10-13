package org.saurus.chess.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.file.Files;
import java.util.List;

import org.saurus.chess.pgn.Board;
import org.saurus.chess.pgn.Game;
import org.saurus.chess.pgn.PGNException;
import org.saurus.chess.pgn.PGNReader;
import org.saurus.net.get.HttpGet;

public class Test {
	public static void main(String[] args) throws IOException {
		Board board = new Board();

		// String fen = "K7/2k5/8/8/8/8/8/8 w - - 0 1";
		// KRK in a trivial position.

		// String fen = "k7/8/K6R/8/8/8/8/8 w - - 0 1";
		// a "real" position from a game with itself:

		// String fen =
		// "r1b1k1nr/p1pp1pp1/np2p3/2b1P2P/1P5P/3B4/P2P1P1N/RNBQK2q w Qkq - 0 15";

		// String fen = "5k2/1p4N1/pPp1PP2/P1Pp1B1N/3P2R1/4K3/8/8 w - - 0 1";
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		PGNReader.setFEN(board, fen);

		String[] dump = board.dump(false);
		for (int i = 0; i < dump.length; i++) {
			System.out.println("BOARD: " + dump[i]);
		}

		PGNReader pgnReader = new PGNReader();
		// Path path = FileSystems.getDefault().getPath("game.pgn");
		Reader reader = HttpGet.get("http://tcec.chessdom.com/live/LiveTmp.pgn");

		// writeFile("lastGame.txt", stream);

		// List<Game> games = reader.read("game.pgn");
		List<Game> games = pgnReader.read(reader);

		try {
			pgnReader.fixupMoves(games.get(0));
		} catch (PGNException e) {
			System.out.println("ERROR: " + e);
			e.printStackTrace();
		}

		// String pgn = readFile("game.pgn");
		// ReadPGN r = new ReadPGN(pgn);
		//
		// System.out.println("game=" + r.getBlackName());

		// game.
	}

	public static void writeFile(String fileName, Reader content) {
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream("filename.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null)
				out.close();
		}
	}

	public static String readFile(String filename) {
		File f = new File(filename);
		try {
			byte[] bytes = Files.readAllBytes(f.toPath());
			return new String(bytes, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
