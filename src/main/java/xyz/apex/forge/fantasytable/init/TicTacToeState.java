package xyz.apex.forge.fantasytable.init;

public enum TicTacToeState
{
	CROSS,
	NOUGHT,
	EMPTY;

	public TicTacToeState opponent()
	{
		return isEmpty() || isCross() ? NOUGHT : CROSS;
	}

	public boolean isCross()
	{
		return is(CROSS);
	}

	public boolean isNought()
	{
		return is(NOUGHT);
	}

	public boolean isEmpty()
	{
		return is(EMPTY);
	}

	public boolean is(TicTacToeState other)
	{
		return this == other;
	}
}
