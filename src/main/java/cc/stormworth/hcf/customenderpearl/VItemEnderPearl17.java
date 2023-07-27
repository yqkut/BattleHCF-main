package cc.stormworth.hcf.customenderpearl;

import net.minecraft.server.v1_7_R4.*;

public class VItemEnderPearl17 extends ItemEnderPearl {
   public VItemEnderPearl17() {
      this.maxStackSize = 16;
      this.a(CreativeModeTab.f);
   }

   public ItemStack a(ItemStack stack, World world, EntityHuman entity) {
      if (!entity.abilities.canInstantlyBuild) {
         --stack.count;
         world.makeSound(entity, "random.bow", 0.5F, 0.4F / (g.nextFloat() * 0.4F + 0.8F));
         if (!world.isStatic) {
            world.addEntity(new VEntityEnderPearl17(world, entity));
         }

      }
      return stack;
   }
}
