package mod.akrivus.revolution.entity.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mod.akrivus.revolution.Revolution;
import mod.akrivus.revolution.entity.EntityFemale;
import mod.akrivus.revolution.entity.EntityHuman;
import mod.akrivus.revolution.entity.EntityMale;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.SoundEvents;

public class EntityAIBreedInter extends EntityAIBase {
    private EntityFemale candidate;
    private EntityMale male;
    private double moveSpeed;
    public EntityAIBreedInter(EntityMale male, double speed) {
        this.moveSpeed = speed;
        this.male = male;
        this.setMutexBits(4);
    }
    public boolean shouldExecute() {
        if (this.male.isOldEnoughToBreed() && this.male.isAroused() && this.male.ticksExisted % 100 == 0) {
		    List<EntityFemale> list = this.male.world.<EntityFemale>getEntitiesWithinAABB(EntityFemale.class, this.male.getEntityBoundingBox().grow(8.0D, 4.0D, 8.0D));
		    double maxDistance = Double.MAX_VALUE;
		    this.candidate = null;
		    for (EntityFemale human : list) {
		        if (human.isOldEnoughToBreed() && human.isAroused()) {
		        	double dist = this.male.getDistanceSq(human);
		            if (dist < maxDistance) {
		            	this.candidate = human;
		            	maxDistance = dist;
		            }
		        }
		    }
		    return this.candidate != null;
        }
		return false;
    }
    public void startExecuting() {
    	this.male.getNavigator().tryMoveToEntityLiving(this.candidate, this.moveSpeed);
    }
    public boolean shouldContinueExecuting() {
       	return !this.candidate.isEntityAlive() && this.candidate.isAroused() && this.candidate.getDistanceSq(this.male) < 256.0D;
    }
    public void updateTask() {
        this.male.getNavigator().tryMoveToEntityLiving(this.candidate, this.moveSpeed);
        if (this.male.getDistanceSq(this.candidate) < 2.0D) {
        	EntityHuman child = this.candidate.createChild(this.male);
    		this.candidate.depleteFoodLevels(this.candidate.getFoodLevels());
        	this.candidate.setSickness(this.male.getImmuneStrength());
    		this.candidate.world.spawnEntity(child);
        	this.candidate.setIsFertile(false);
        	this.candidate.dropItem(Revolution.MAN_MEAT, 1);
        	List<UUID> femaleMemory = new ArrayList<UUID>();
        	femaleMemory.addAll(this.male.getMemories());
        	femaleMemory.removeAll(this.candidate.getMemories());
            for (UUID id : femaleMemory) {
            	this.candidate.learnMemory(id);
            }
            List<UUID> maleMemory = new ArrayList<UUID>();
        	maleMemory.addAll(this.candidate.getMemories());
        	maleMemory.removeAll(this.male.getMemories());
            for (UUID id : maleMemory) {
            	this.male.learnMemory(id);
            }
        }
    }
    public void resetTask() {
        this.candidate = null;
    }
}