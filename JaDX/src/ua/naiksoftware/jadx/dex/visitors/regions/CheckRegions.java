package jadx.dex.visitors.regions;

import android.util.Log;
import jadx.dex.attributes.AttributeFlag;
import jadx.dex.nodes.BlockNode;
import jadx.dex.nodes.IBlock;
import jadx.dex.nodes.IRegion;
import jadx.dex.nodes.MethodNode;
import jadx.dex.regions.LoopRegion;
import jadx.dex.visitors.AbstractVisitor;
import jadx.utils.ErrorsCounter;
import jadx.utils.exceptions.JadxException;

import java.util.HashSet;
import java.util.Set;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class CheckRegions extends AbstractVisitor {
	private final String tag = getClass().getName();

	@Override
	public void visit(MethodNode mth) throws JadxException {
		if (mth.isNoCode() || mth.getBasicBlocks().size() == 0) {
                return;
            }

		// check if all blocks included in regions
		final Set<BlockNode> blocksInRegions = new HashSet<BlockNode>();
		IRegionVisitor collectBlocks = new AbstractRegionVisitor() {
			@Override
			public void processBlock(MethodNode mth, IBlock container) {
				if (container instanceof BlockNode) {
                                blocksInRegions.add((BlockNode) container);
                            }
				else {
                                Log.w(tag, "Not block node : " + container.getClass().getSimpleName());
                            }
			}
		};
		DepthRegionTraverser.traverseAll(mth, collectBlocks);

		if (mth.getBasicBlocks().size() != blocksInRegions.size()) {
                mth.getAttributes().add(AttributeFlag.INCONSISTENT_CODE);
            }

		// check loop conditions
		IRegionVisitor checkLoops = new AbstractRegionVisitor() {
			@Override
			public void enterRegion(MethodNode mth, IRegion region) {
				if (region instanceof LoopRegion) {
					LoopRegion loop = (LoopRegion) region;
					if (loop.getConditionBlock() != null
							&& loop.getConditionBlock().getInstructions().size() != 1) {
						ErrorsCounter.methodError(mth, "Incorrect condition in loop: " + loop.getConditionBlock());
						mth.getAttributes().add(AttributeFlag.INCONSISTENT_CODE);
					}
				}
			}
		};
		DepthRegionTraverser.traverseAll(mth, checkLoops);

	}
}
