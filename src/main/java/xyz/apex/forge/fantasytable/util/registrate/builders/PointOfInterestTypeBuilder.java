package xyz.apex.forge.fantasytable.util.registrate.builders;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.village.PointOfInterestType;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class PointOfInterestTypeBuilder<T extends PointOfInterestType, P> extends AbstractBuilder<PointOfInterestType, T, P, PointOfInterestTypeBuilder<T, P>>
{
	private final PointOfInterestTypeFactory<T> factory;
	private NonNullSupplier<Block> block = () -> Blocks.AIR;
	private int maxTickets = 1;
	private BiPredicate<PointOfInterestType, T> predicate = (testType, thisType) -> testType == thisType;
	private int validRange = 1;

	protected PointOfInterestTypeBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, PointOfInterestTypeFactory<T> factory)
	{
		super(owner, parent, name, callback, PointOfInterestType.class);

		this.factory = factory;
	}

	public PointOfInterestTypeBuilder<T, P> matchingBlock(NonNullSupplier<Block> block)
	{
		this.block = block;
		return this;
	}

	public PointOfInterestTypeBuilder<T, P> maxTickets(int maxTickets)
	{
		this.maxTickets = Math.max(this.maxTickets, maxTickets);
		return this;
	}

	public PointOfInterestTypeBuilder<T, P> predicate(BiPredicate<PointOfInterestType, T> predicate)
	{
		this.predicate = predicate;
		return this;
	}

	public PointOfInterestTypeBuilder<T, P> validRange(int validRange)
	{
		this.validRange = Math.max(validRange, 1);
		return this;
	}

	@Override
	protected @NonnullType T createEntry()
	{
		AtomicReference<T> result = new AtomicReference<>();
		Predicate<PointOfInterestType> predicate = poiType -> this.predicate.test(poiType, result.get());
		Set<BlockState> matchingBlockStates = PointOfInterestType.getBlockStates(block.get());
		String registryName = String.format("%s:%s", getOwner().getModid(), getName());
		result.set(factory.create(registryName, matchingBlockStates, maxTickets, predicate, validRange));
		return result.get();
	}

	public static <T extends PointOfInterestType, P> PointOfInterestTypeBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, PointOfInterestTypeFactory<T> factory)
	{
		return new PointOfInterestTypeBuilder<>(owner, parent, name, callback, factory);
	}

	@FunctionalInterface
	public interface PointOfInterestTypeFactory<T extends PointOfInterestType>
	{
		T create(String name, Set<BlockState> matchingStates, int maxTickets, Predicate<PointOfInterestType> predicate, int validRange);
	}
}
