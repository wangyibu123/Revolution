package mod.akrivus.revolution.entity.ai;

import java.util.List;

import mod.akrivus.revolution.entity.EntityHuman;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;

public class EntityAIPickUpItems extends EntityAIBase {
	private final EntityHuman human;
	private final double movementSpeed;
	private EntityItem item;

	public EntityAIPickUpItems(EntityHuman entityIn, double movementSpeedIn) {
		this.human = entityIn;
		this.movementSpeed = movementSpeedIn;
		this.setMutexBits(1);
	}
	@Override
	public boolean shouldExecute() {
		List<EntityItem> list = this.human.world.<EntityItem>getEntitiesWithinAABB(EntityItem.class, this.human.getEntityBoundingBox().grow(16.0D, 4.0D, 16.0D));
		double maxDistance = Double.MAX_VALUE;
		for (EntityItem item : list) {
			double newDistance = this.human.getDistanceSq(item);
			if (newDistance <= maxDistance && !item.isDead) {
				maxDistance = newDistance;
				this.item = item;
			}
		}
		return this.item != null && !this.item.isDead && this.human.canPickUpLoot();
	}
	@Override
	public boolean shouldContinueExecuting() {
		return this.item != null && !this.item.isDead && !this.human.getNavigator().noPath();
	}
	@Override
	public void startExecuting() {
		this.human.getLookHelper().setLookPositionWithEntity(this.item, 30.0F, 30.0F);
	}
	@Override
	public void resetTask() {
		this.human.getNavigator().clearPath();
		this.item = null;
	}
	@Override
	public void updateTask() {
		if (this.human.getDistanceSq(this.item) > 0.0F) {
		   	this.human.getNavigator().tryMoveToEntityLiving(this.item, this.movementSpeed);
		}
	}
}