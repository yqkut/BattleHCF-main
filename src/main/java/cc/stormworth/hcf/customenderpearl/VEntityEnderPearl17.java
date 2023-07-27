package cc.stormworth.hcf.customenderpearl;

import cc.stormworth.hcf.customenderpearl.event.PlayerCrossPearlEvent;
import cc.stormworth.hcf.util.cooldown.CooldownAPI;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.material.Gate;
import org.bukkit.material.Stairs;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class VEntityEnderPearl17 extends EntityEnderPearl {

   private ImmutableSet<Material> Blocked;

   private boolean ar;

   private boolean isEdited;

   private EntityLiving c;

   private boolean crosspearl;

   private List<BlockFace> getBFByDirection(String s) {
      List<BlockFace> xd = new ArrayList<>();
      switch (s) {
         case "E":
            xd.add(BlockFace.EAST);
            return xd;
         case "N":
            xd.add(BlockFace.NORTH);
            return xd;
         case "W":
            xd.add(BlockFace.WEST);
            return xd;
         case "S":
            xd.add(BlockFace.SOUTH);
            return xd;
         case "NW":
            xd.add(BlockFace.NORTH);
            xd.add(BlockFace.WEST);
            return xd;
         case "SW":
            xd.add(BlockFace.SOUTH);
            xd.add(BlockFace.WEST);
            return xd;
         case "NE":
            xd.add(BlockFace.NORTH);
            xd.add(BlockFace.EAST);
            return xd;
         case "SE":
            xd.add(BlockFace.SOUTH);
            xd.add(BlockFace.EAST);
            return xd;
         default:
            return null;
      }
   }

   protected boolean checkPearlThru(Location location, Player player) {
      Location l = player.getLocation();
      String s = Utils.direction(l);
      Block block = this.getThruBlock(location, player);
      if (block != null) {
         Material type = block.getType();
         BlockFace face = null;
         Location ll = block.getLocation().clone();
         Block bb = block.getRelative(BlockFace.WEST);
         Block eastB = block.getRelative(BlockFace.EAST);
         Block northB = block.getRelative(BlockFace.NORTH);
         Block southB = block.getRelative(BlockFace.SOUTH);

         boolean stairs = Utils.stairs(type);
         boolean typee = Utils.diagonalPearl(type);

         if (stairs) {
            Stairs str = (Stairs)block.getState().getData();
            face = str.getFacing();
         }

         if (this.crosspearl) {
            return true;
         }

         boolean tali = EnderPearlSettings.PEARL_TAIL_TELEPORT;
         double y = EnderPearlSettings.PEARL_TAIL_TELEPORT_Y;
         double z = EnderPearlSettings.PEARL_TAIL_TELEPORT_Z;
         boolean b;
         int ii;
         Block b2;
         Block b3;
         if (s.contains("E")) {
            if (s.equals("E")) {
               if (stairs && face != null && face != BlockFace.SOUTH && face != BlockFace.NORTH && face != BlockFace.WEST) {
                  return false;
               }
               b = !Utils.thruEnabled(eastB.getType()) && (Utils.customTransparent(eastB) || !this.ar && Utils.customTransparent(eastB.getRelative(BlockFace.DOWN)));

               if (!b) {
                  for(ii = 0; ii < EnderPearlSettings.PEARL_MAX_PEARL_PASS_THRU_BLOCKS; ++ii) {
                     b2 = block.getRelative(BlockFace.EAST);
                     if (Utils.thruEnabled(b2.getType())) {
                        b3 = b2.getRelative(BlockFace.EAST);
                        if (!Utils.thruEnabled(b3.getType())) {
                           if (Utils.customTransparent(b3) || !this.ar && b3.getRelative(BlockFace.UP).getType().isSolid() && Utils.customTransparent(b3.getRelative(BlockFace.DOWN))) {
                              return false;
                           }

                           if (!this.isStairGood(b2, s)) {
                              return false;
                           }

                           location.setX(location.getX() + 1.5D);
                           break;
                        }

                        if (!this.isStairGood(b2, s)) {
                           return false;
                        }

                        block = b2;
                        location.setX(location.getX() + (tali ? z : 1.0D));
                     }
                  }
               }

               if (block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getType().isSolid()) {
                  if (!Utils.critblock(block.getType())) {
                     return false;
                  }

                  if (b) {
                     return false;
                  }

                  if (tali) {
                     location.setY((double)block.getY() - y);
                  } else {
                     location.setX(location.getX() + 1.0D);
                  }

                  this.setToBlock(location);
               }
            }

            if (s.equals("SE") && Utils.customTransparent(eastB) && Utils.customTransparent(southB)) {
               return false;
            }

            if (s.equals("NE") && Utils.customTransparent(eastB) && Utils.customTransparent(northB)) {
               return false;
            }

            if (s.equals("NE") && typee) {
               if (Utils.diagonalPearl(southB.getType()) || Utils.diagonalPearl(bb.getType())) {
                  if (!Utils.customTransparent(northB)) {
                     location.setZ(ll.add(0.0D, 0.0D, -1.0D).getZ());
                     location.setX(ll.getX() + 0.5D);
                     location.setZ(ll.getZ() + 0.5D);
                     this.isEdited = true;
                     return true;
                  }

                  location.setX(ll.add(1.0D, 0.0D, 0.0D).getX());
                  location.setX(ll.getX() + 0.5D);
                  location.setZ(ll.getZ() + 0.5D);
                  this.isEdited = true;
                  return true;
               }
            } else if (s.equals("SE") && typee && (Utils.diagonalPearl(northB.getType()) || Utils.diagonalPearl(bb.getType()))) {
               if (!Utils.customTransparent(southB)) {
                  location.setZ(ll.add(0.0D, 0.0D, 1.0D).getZ());
                  location.setX(ll.getX() + 0.5D);
                  location.setZ(ll.getZ() + 0.5D);
                  this.isEdited = true;
                  return true;
               }

               location.setX(ll.add(1.0D, 0.0D, 0.0D).getX());
               location.setX(ll.getX() + 0.5D);
               location.setZ(ll.getZ() + 0.5D);
               this.isEdited = true;
               return true;
            }

            if (!this.isEdited) {
               location.setX((double)location.getBlockX() + 0.5D);
               location.setZ((double)location.getBlockZ() + 0.5D);
               this.isEdited = true;
            }

            return true;
         }

         if (s.contains("W")) {
            if (s.equals("W")) {
               if (stairs && face != null && face != BlockFace.SOUTH && face != BlockFace.NORTH && face != BlockFace.EAST) {
                  return false;
               }
               b = !Utils.thruEnabled(bb.getType()) && (Utils.customTransparent(bb) || !this.ar && Utils.customTransparent(bb.getRelative(BlockFace.DOWN)));

               if (!b) {
                  for(ii = 0; ii < EnderPearlSettings.PEARL_MAX_PEARL_PASS_THRU_BLOCKS; ++ii) {
                     b2 = block.getRelative(BlockFace.WEST);
                     if (Utils.thruEnabled(b2.getType())) {
                        b3 = b2.getRelative(BlockFace.WEST);
                        if (!Utils.thruEnabled(b3.getType())) {
                           if (Utils.customTransparent(b3) || !this.ar && b3.getRelative(BlockFace.UP).getType().isSolid() && Utils.customTransparent(b3.getRelative(BlockFace.DOWN))) {
                              return false;
                           }

                           if (!this.isStairGood(b2, s)) {
                              return false;
                           }

                           location.setX(location.getX() - 1.5D);
                           break;
                        }

                        if (!this.isStairGood(b2, s)) {
                           return false;
                        }

                        block = b2;
                        location.setX(location.getX() - (tali ? z : 1.0D));
                     }
                  }
               }

               if (block.getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getType().isSolid()) {
                  if (!Utils.critblock(block.getType()) || b) {
                     return false;
                  }

                  if (tali) {
                     location.setY((double)block.getY() - y);
                  } else {
                     location.setX(location.getX() - 1.0D);
                  }

                  this.setToBlock(location);
               }
            }

            if (s.equals("SW")) {
               if (Utils.customTransparent(bb) && Utils.customTransparent(southB)) {
                  return false;
               }
            } else if (s.equals("NW") && Utils.customTransparent(bb) && Utils.customTransparent(northB)) {
               return false;
            }

            if (s.equals("NW") && typee) {
               if (Utils.diagonalPearl(eastB.getType()) || Utils.diagonalPearl(southB.getType())) {
                  if (!Utils.customTransparent(northB)) {
                     location.setZ(ll.add(0.0D, 0.0D, -1.0D).getZ());
                     location.setX(ll.getX() + 0.5D);
                     location.setZ(ll.getZ() + 0.5D);
                     this.isEdited = true;
                     return true;
                  }

                  location.setX(ll.add(-1.0D, 0.0D, 0.0D).getX());
                  location.setX(ll.getX() + 0.5D);
                  location.setZ(ll.getZ() + 0.5D);
                  this.isEdited = true;
                  return true;
               }
            } else if (s.equals("SW") && typee && (Utils.diagonalPearl(northB.getType()) || Utils.diagonalPearl(eastB.getType()))) {
               if (!Utils.customTransparent(southB)) {
                  location.setZ(ll.add(0.0D, 0.0D, 1.0D).getZ());
                  location.setX(ll.getX() + 0.5D);
                  location.setZ(ll.getZ() + 0.5D);
                  this.isEdited = true;
                  return true;
               }

               location.setX(ll.add(-1.0D, 0.0D, 0.0D).getX());
               location.setX(ll.getX() + 0.5D);
               location.setZ(ll.getZ() + 0.5D);
               this.isEdited = true;
               return true;
            }

            if (!this.isEdited) {
               location.setX((double)location.getBlockX() + 0.5D);
               location.setZ((double)location.getBlockZ() + 0.5D);
               this.isEdited = true;
            }

            return true;
         }

         if (s.equals("N")) {
            if (stairs && face != null && face != BlockFace.EAST && face != BlockFace.WEST && face != BlockFace.SOUTH) {
               return false;
            }

            b = false;
            if (!Utils.thruEnabled(northB.getType()) && (Utils.customTransparent(northB) || !this.ar && Utils.customTransparent(northB.getRelative(BlockFace.DOWN)))) {
               b = true;
            }

            if (!b) {
               for(ii = 0; ii < EnderPearlSettings.PEARL_MAX_PEARL_PASS_THRU_BLOCKS; ++ii) {
                  b2 = block.getRelative(BlockFace.NORTH);
                  if (Utils.thruEnabled(b2.getType())) {
                     b3 = b2.getRelative(BlockFace.NORTH);
                     if (!Utils.thruEnabled(b3.getType())) {
                        if (Utils.customTransparent(b3) || !this.ar && b3.getRelative(BlockFace.UP).getType().isSolid() && Utils.customTransparent(b3.getRelative(BlockFace.DOWN))) {
                           return false;
                        }

                        if (!this.isStairGood(b2, s)) {
                           return false;
                        }

                        location.setZ(location.getZ() - 1.5D);
                        break;
                     }

                     if (!this.isStairGood(b2, s)) {
                        return false;
                     }

                     block = b2;
                     location.setZ(location.getZ() - (tali ? z : 1.0D));
                  }
               }
            }

            if (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).getType().isSolid()) {
               if (!Utils.critblock(block.getType()) || b) {
                  return false;
               }

               if (tali) {
                  location.setY((double)block.getY() - y);
               } else {
                  location.setZ(location.getZ() - 1.0D);
               }

               this.setToBlock(location);
            }

            if (!this.isEdited) {
               location.setX((double)location.getBlockX() + 0.5D);
               location.setZ((double)location.getBlockZ() + 0.5D);
               this.isEdited = true;
            }

            return true;
         }

         if (s.equals("S")) {
            if (stairs && face != null && face != BlockFace.EAST && face != BlockFace.WEST && face != BlockFace.NORTH) {
               return false;
            }

            b = false;
            if (!Utils.thruEnabled(southB.getType()) && (Utils.customTransparent(southB) || !this.ar && Utils.customTransparent(southB.getRelative(BlockFace.DOWN)))) {
               b = true;
            }

            location.setZ(location.getZ() + (tali ? z : 0.5D));
            ii = 0;

            while(true) {
               label599: {
                  if (ii < EnderPearlSettings.PEARL_MAX_PEARL_PASS_THRU_BLOCKS) {
                     b2 = block.getRelative(BlockFace.SOUTH);
                     if (!Utils.thruEnabled(b2.getType())) {
                        break label599;
                     }

                     b3 = b2.getRelative(BlockFace.SOUTH);
                     if (Utils.thruEnabled(b3.getType())) {
                        if (!this.isStairGood(b2, s)) {
                           return false;
                        }

                        block = b2;
                        location.setZ(location.getZ() + (tali ? z : 1.0D));
                        break label599;
                     }

                     if (Utils.customTransparent(b3) || !this.ar && b3.getRelative(BlockFace.UP).getType().isSolid() && Utils.customTransparent(b3.getRelative(BlockFace.DOWN))) {
                        return false;
                     }

                     if (!this.isStairGood(b2, s)) {
                        return false;
                     }

                     location.setZ(location.getZ() + 1.5D);
                  }

                  if (block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getType().isSolid()) {
                     if (!Utils.critblock(block.getType()) || b) {
                        return false;
                     }

                     if (tali) {
                        location.setY((double)block.getY() - y);
                     } else {
                        location.setZ(location.getZ() + 1.0D);
                     }

                     this.setToBlock(location);
                  }

                  if (!this.isEdited) {
                     location.setX((double)location.getBlockX() + 0.5D);
                     location.setZ((double)location.getBlockZ() + 0.5D);
                     this.isEdited = true;
                  }

                  return true;
               }

               ++ii;
            }
         }
      }

      return true;
   }

   public void refundPearl(Player player) {
      player.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.ENDER_PEARL, 1));
      player.updateInventory();

      CooldownAPI.removeCooldown(player, "enderpearl");
   }

   protected void a(MovingObjectPosition position) {
      EntityLiving shooter = this.getShooter();
      Block block;
      if (this.world.getType(position.b, position.c, position.d) != null) {
         net.minecraft.server.v1_7_R4.Block bb = this.world.getType(position.b, position.c, position.d);
         if (bb == Blocks.FENCE_GATE && EnderPearlSettings.THRU_FENCES || bb == Blocks.WEB && EnderPearlSettings.THRU_COWEB || bb == Blocks.TRIPWIRE && EnderPearlSettings.THRU_STRING) {
            BlockIterator iterator = null;

            try {
               Vector vctor = new Vector(this.locX, this.locY, this.locZ);
               Vector vector = new Vector(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
               Vector vktor = (new Vector(vector.getX() - vctor.getX(), vector.getY() - vctor.getY(), vector.getZ() - vctor.getZ())).normalize();
               iterator = new BlockIterator(this.world.getWorld(), vctor, vktor, 0.0D, 1);
            } catch (IllegalStateException ignored) {}

            if (iterator != null) {
               boolean b2 = false;
               boolean vector = false;
               boolean vktor = false;
               boolean b3 = true;

               while(true) {
                  if (!iterator.hasNext()) {
                     if (b2 && !vector || vktor && b3) {
                        return;
                     }
                     break;
                  }

                  block = iterator.next();
                  String ss = Utils.direction(shooter.getBukkitEntity().getLocation());
                  List<BlockFace> xdd = this.getBFByDirection(ss);
                  if (block.getType().isSolid() && block.getType().isOccluding()) {
                     vector = true;
                  }

                  if (bb == Blocks.WEB || bb == Blocks.TRIPWIRE) {
                     vktor = true;
                     Block block2 = this.getBukkitEntity().getLocation().getBlock();
                     Material material = this.getBukkitEntity().getLocation().getBlock().getType();
                     if (material == Material.AIR || material == Material.WEB || material == Material.TRIPWIRE) {
                        if (xdd != null) {
                           if (this.isntDiagonal(ss)) {
                              if (Utils.customTransparent(block.getRelative(xdd.get(0)))) {
                                 b3 = false;
                              }
                           } else if (Utils.customTransparent(block.getRelative(xdd.get(0))) || Utils.customTransparent(block.getRelative(xdd.get(1)))) {
                              b3 = false;
                           }
                        }

                        if (material == Material.AIR && (block2.getRelative(BlockFace.UP).getType() == Material.WEB ||
                                block2.getRelative(BlockFace.UP).getType() == Material.TRIPWIRE) && !b3) {
                           b3 = true;
                        }
                     }
                  }

                  if (block.getState().getData() instanceof Gate && ((Gate)block.getState().getData()).isOpen()) {
                     b2 = true;
                  }

                  if (b2) {
                     if (!this.isntDiagonal(ss)) {
                        if (Utils.customTransparent(block.getRelative(xdd.get(0)))) {
                           vector = true;
                        } else if (Utils.customTransparent(block.getRelative(xdd.get(1)))) {
                           vector = true;
                        }
                     } else if (Utils.customTransparent(block.getRelative(xdd.get(0)))) {
                        vector = true;
                     }

                     if (block.getState().getData() instanceof Gate && !((Gate)block.getState().getData()).isOpen()) {
                        vector = true;
                     }
                  }
               }
            }
         }
      }

      if (position.entity != null) {
         if (position.entity == this.c) {
            return;
         }

         position.entity.damageEntity(DamageSource.projectile(this, shooter), 0.0F);
      }

      if (this.inUnloadedChunk && this.world.paperSpigotConfig.removeUnloadedEnderPearls) {
         this.die();
      }

      if (EnderPearlSettings.PEARL_PARTICLES) {
         for(int iii = 0; iii < EnderPearlSettings.PEARL_PARTICLES_AMOUNT; ++iii) {
            this.world.addParticle("portal", this.locX, this.locY + this.random.nextDouble() * 2.0D, this.locZ, this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
         }
      }

      if (!this.world.isStatic) {
         if (shooter instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)shooter;
            if (player.playerConnection.b().isConnected() && player.world == this.world && !player.isSleeping()) {
               CraftPlayer vector = player.getBukkitEntity();
               Location ll = vector.getLocation();
               Location b3 = this.getBukkitEntity().getLocation();

               if (this.checkForEntity(b3, vector, position)) {
                  return;
               }


               b3.setPitch(ll.getPitch());
               b3.setYaw(ll.getYaw());
               block = b3.getBlock();
               Material typee = block.getType();
               String s5 = typee.toString();
               if ((Utils.slab(typee) || Utils.stairs(typee) || s5.contains("CHEST") || s5.equals("IRON_TRADOOR") ||
                       s5.equals("TRAP_DOOR") || s5.contains("ANVIL") || s5.contains("DAYLIGHT") || s5.contains("WALL") ||
                       s5.contains("BED") || s5.contains("PISTON") || typee == Material.ENDER_PORTAL_FRAME ||
                       typee == Material.ENCHANTMENT_TABLE) && !Utils.thruEnabled(typee)) {
                  refundPearl(vector);
                  this.die();
                  return;
               }

               if (s5.equals("IRON_TRADOOR") || s5.equals("TRAP_DOOR") && (ll.getBlock().getRelative(BlockFace.UP).getType() == Material.TRAP_DOOR || ll.getBlock().getRelative(BlockFace.UP).getType().toString().equals("IRON_TRAPDOOR"))) {
                  refundPearl(vector);
                  this.die();
                  return;
               }

               if (!this.checkPearlThru(b3, vector)) {
                  refundPearl(vector);
                  this.die();
                  return;
               }

               if (!this.isEdited) {
                  if (EnderPearlSettings.REFUND_RISKY_PEARL && !this.checkRisky(b3, vector, position)) {
                     refundPearl(vector);
                     this.die();
                     return;
                  }

                  if (EnderPearlSettings.REFUND_IF_SO_CLOSE && !this.checkClose(b3, vector, position)) {
                     refundPearl(vector);
                     this.die();
                     return;
                  }

                  if (EnderPearlSettings.REFUND_PEARL_IF_SUFFOCATING && Utils.customTransparent(block)) {
                     refundPearl(vector);
                     this.die();
                     return;
                  }
               }

               if (EnderPearlSettings.FIX_WALLS_GLITCH) {
                  if (!this.isEdited) {
                     this.setToBlock(b3);
                  } else if (this.Blocked.contains(typee)) {
                     refundPearl(vector);
                     this.die();
                     return;
                  }
               }

               if (EnderPearlSettings.FIX_FENCE_GATE_GLITCH && !this.checkFenceGate(b3, vector)) {
                  refundPearl(vector);
                  this.die();
                  return;
               }

               PlayerTeleportEvent event = new PlayerTeleportEvent(vector, vector.getLocation(), b3, TeleportCause.ENDER_PEARL);
               Bukkit.getPluginManager().callEvent(event);

               if (!event.isCancelled() && !player.playerConnection.isDisconnected()) {
                  if (player.am()) {
                     player.mount(null);
                  }

                  player.playerConnection.teleport(event.getTo());
                  player.fallDistance = 0.0F;
                  CraftEventFactory.entityDamage = this;
                  player.damageEntity(DamageSource.FALL, (float) EnderPearlSettings.PEARL_DAMAGE);
                  CraftEventFactory.entityDamage = null;
               }
            }
         }

         this.die();
      }

   }

   protected boolean checkForEntity(Location location, Player player, MovingObjectPosition positon) {
      if (positon.entity != null) {

         if (positon.entity == this.c) {
            return false;
         }

         if (EnderPearlSettings.BETTER_HIT_DETECTION) {
            String s = Utils.direction(location);
            Block block = location.getBlock();
            List<BlockFace> faces = this.getBFByDirection(s);

            if (!this.isntDiagonal(s)) {
               if (!Utils.customTransparent(block.getRelative(faces.get(0))) && !Utils.customTransparent(block.getRelative(faces.get(1)))) {
                  location.setX(positon.entity.locX);
                  location.setZ(positon.entity.locZ);
               }
            } else {
               Block front = block.getRelative(faces.get(0));

               if (front != null){
                  location.setX(positon.entity.locX);
                  location.setZ(positon.entity.locZ);
                  return false;
               }

            }
         }

         return positon.entity.getBukkitEntity().getLocation().distance(player.getLocation()) <= 0.5D && EnderPearlSettings.GET_OUT_FROM_ONE_BY_ONE;
      }

      return false;
   }

   protected boolean checkClose(Location location, Player player, MovingObjectPosition position) {
      if (position.entity != null) {
         return true;
      } else {
         Block b1 = player.getTargetBlock(null, 1);
         Block b2 = location.getBlock();
         String s = Utils.direction(player.getLocation());
         Block b3 = b2.getRelative(BlockFace.WEST);
         Block b4 = b2.getRelative(BlockFace.EAST);
         Block b5 = b2.getRelative(BlockFace.NORTH);
         Block b6 = b2.getRelative(BlockFace.SOUTH);
         if (location.distance(player.getLocation()) <= 1.6D && player.getLocation().distance(b1.getLocation().clone().add(0.5D, 0.0D, 0.5D)) <= 1.6D) {
            if (Utils.thruEnabled(b2.getType()) || Utils.thruEnabled(b1.getRelative(BlockFace.DOWN).getType())) {
               return true;
            }

            if (Utils.customTransparent(b1)) {
               return false;
            }
         }

         if (!Utils.customTransparent(b2)) {
            Location ll = player.getLocation();
            double b = EnderPearlSettings.REFUND_IF_SO_CLOSE_DISTANCE;
            if (s.contains("N")) {
               if (Utils.customTransparent(b5) && this.distance(b5, ll, b)) {
                  return false;
               }
            } else if (s.contains("S")) {
               if (Utils.customTransparent(b6) && this.distance(b6, ll, b)) {
                  return false;
               }
            } else if (s.contains("W")) {
               if (Utils.customTransparent(b3) && this.distance(b3, ll, b)) {
                  return false;
               }
            } else if (s.contains("E") && Utils.customTransparent(b4) && this.distance(b4, ll, b)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isntDiagonal(String s) {
      return s.equals("W") || s.equals("S") || s.equals("E") || s.equals("N");
   }

   protected Block getThruBlock(Location location, Player player) {
      Block bb = location.getBlock();
      Location l2 = player.getLocation();
      String direction = Utils.direction(l2);
      Material type = bb.getType();
      Block b2 = bb.getRelative(BlockFace.DOWN);
      Block b3 = bb.getRelative(BlockFace.WEST);
      Block b4 = bb.getRelative(BlockFace.EAST);
      Block b5 = bb.getRelative(BlockFace.NORTH);
      Block b6 = bb.getRelative(BlockFace.SOUTH);
      Material typee = b4.getType();
      Material type2 = b3.getType();
      Material type3 = b5.getType();
      Material type4 = b6.getType();
      if (this.distance(bb, l2, 4.0D)) {
         if (Utils.thruEnabled(type)) {
            if (direction.equals("SE")) {
               if (Utils.thruEnabled(typee)) {
                  return b4;
               }

               if (Utils.thruEnabled(type4)) {
                  return b6;
               }
            } else if (direction.equals("NE")) {
               if (Utils.thruEnabled(typee)) {
                  return b4;
               }

               if (Utils.thruEnabled(type3)) {
                  return b5;
               }
            } else if (direction.equals("SW")) {
               if (Utils.thruEnabled(type2)) {
                  return b3;
               }

               if (Utils.thruEnabled(type4)) {
                  return b6;
               }
            } else if (direction.equals("NW")) {
               if (Utils.thruEnabled(type2)) {
                  return b3;
               }

               if (Utils.thruEnabled(type3)) {
                  return b5;
               }
            }

            return bb;
         }

         Block b56;
         if (!Utils.customTransparent(bb) && !Utils.customTransparent(b2)) {
            b56 = player.getTargetBlock(null, 1);
            if (EnderPearlSettings.HIT_THRU_BLOCK) {
               Block b7;
               if (direction.equals("E")) {
                  b7 = b2.getRelative(BlockFace.EAST);
                  if (Utils.thruEnabled(b7.getType()) && b56.getType() == b7.getType()) {
                     location.setX(location.getX() + 0.5D);
                     location.setY((double)b2.getRelative(BlockFace.EAST).getY());
                     return b7;
                  }
               } else if (direction.equals("W")) {
                  b7 = b2.getRelative(BlockFace.WEST);
                  if (Utils.thruEnabled(b7.getType()) && b56.getType() == b7.getType()) {
                     location.setX(location.getX() - 0.5D);
                     location.setY((double)b2.getRelative(BlockFace.WEST).getY());
                     return b7;
                  }
               } else if (direction.equals("N")) {
                  b7 = b2.getRelative(BlockFace.NORTH);
                  if (Utils.thruEnabled(b7.getType()) && b56.getType() == b7.getType()) {
                     location.setY((double)b2.getRelative(BlockFace.NORTH).getY());
                     location.setZ(location.getZ() - 0.5D);
                     return b7;
                  }
               } else if (direction.equals("S")) {
                  b7 = b2.getRelative(BlockFace.SOUTH);
                  if (Utils.thruEnabled(b7.getType()) && b56.getType() == b7.getType()) {
                     location.setZ(location.getZ() + 0.5D);
                     location.setY((double)b2.getRelative(BlockFace.SOUTH).getY());
                     return b7;
                  }
               }
            }
         }

         if (!Utils.customTransparent(bb)) {
            b56 = player.getTargetBlock(null, 3);
            if (b56.getType().toString().contains("WALL") && !Utils.customTransparent(b56.getRelative(BlockFace.UP)) && Utils.getBehind(direction) != null && !Utils.customTransparent(b56.getRelative(Utils.getBehind(direction)).getRelative(BlockFace.UP))) {
               if (!direction.equals("W") && !direction.equals("E")) {
                  if (b56.getRelative(BlockFace.WEST).getType() != Material.AIR || b56.getRelative(BlockFace.EAST).getType() != Material.AIR) {
                     this.ar = true;
                     location.setY(location.getY() - 0.5D);
                     if (direction.equals("N")) {
                        location.setZ(location.getZ() - 0.5D);
                     } else {
                        location.setZ(location.getZ() + 0.5D);
                     }

                     return b56;
                  }
               } else if (b56.getRelative(BlockFace.NORTH).getType() != Material.AIR || b56.getRelative(BlockFace.SOUTH).getType() != Material.AIR) {
                  this.ar = true;
                  location.setY(location.getY() - 0.5D);
                  if (direction.equals("E")) {
                     location.setX(location.getX() + 0.5D);
                  } else {
                     location.setX(location.getX() - 0.5D);
                  }

                  return b56;
               }
            }

            if (b2.getType() != Material.AIR) {
               PlayerCrossPearlEvent event;
               if (direction.equals("SE")) {
                  if (this.isCannable(b4, location)) {
                     if (!Utils.thruEnabled(bb.getRelative(BlockFace.SOUTH_EAST).getType()) && Utils.crossPearl(b4.getType())) {
                        event = new PlayerCrossPearlEvent(player, location, location.clone().add(0.3D, 0.0D, 0.3D));
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                           location.setZ(location.getZ() + 0.3D);
                           location.setX(location.getX() + 0.3D);
                           this.crosspearl = true;
                           this.isEdited = true;
                           return b4;
                        }
                     }

                     return bb.getRelative(BlockFace.SOUTH_EAST);
                  }

                  if (this.isCannable(b6, location)) {
                     if (!Utils.thruEnabled(bb.getRelative(BlockFace.SOUTH_EAST).getType()) && Utils.crossPearl(b6.getType())) {
                        event = new PlayerCrossPearlEvent(player, location, location.clone().add(0.3D, 0.0D, 0.3D));
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                           location.setZ(location.getZ() + 0.3D);
                           location.setX(location.getX() + 0.3D);
                           this.crosspearl = true;
                           this.isEdited = true;
                           return b6;
                        }
                     }

                     return bb.getRelative(BlockFace.SOUTH_EAST);
                  }
               } else if (direction.equals("NE")) {
                  if (this.isCannable(b5, location)) {
                     if (!Utils.thruEnabled(bb.getRelative(BlockFace.NORTH_EAST).getType()) && Utils.crossPearl(b5.getType())) {
                        event = new PlayerCrossPearlEvent(player, location, location.clone().add(0.3D, 0.0D, -0.3D));
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                           location.setZ(location.getZ() - 0.3D);
                           location.setX(location.getX() + 0.3D);
                           this.crosspearl = true;
                           this.isEdited = true;
                           return b5;
                        }
                     }

                     return bb.getRelative(BlockFace.NORTH_EAST);
                  }

                  if (this.isCannable(b4, location)) {
                     if (!Utils.thruEnabled(bb.getRelative(BlockFace.NORTH_EAST).getType()) && Utils.crossPearl(b4.getType())) {
                        event = new PlayerCrossPearlEvent(player, location, location.clone().add(0.3D, 0.0D, -0.3D));
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                           location.setZ(location.getZ() - 0.3D);
                           location.setX(location.getX() + 0.3D);
                           this.crosspearl = true;
                           this.isEdited = true;
                           return b4;
                        }
                     }

                     return bb.getRelative(BlockFace.NORTH_EAST);
                  }
               } else if (direction.equals("NW")) {
                  if (this.isCannable(b3, location)) {
                     if (!Utils.thruEnabled(bb.getRelative(BlockFace.NORTH_WEST).getType()) && Utils.crossPearl(b3.getType())) {
                        event = new PlayerCrossPearlEvent(player, location, location.clone().add(-0.3D, 0.0D, -0.3D));
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                           location.setZ(location.getZ() - 0.3D);
                           location.setX(location.getX() - 0.3D);
                           this.crosspearl = true;
                           this.isEdited = true;
                           return b3;
                        }
                     }

                     return bb.getRelative(BlockFace.NORTH_WEST);
                  }

                  if (this.isCannable(b5, location)) {
                     if (!Utils.thruEnabled(bb.getRelative(BlockFace.NORTH_WEST).getType()) && Utils.crossPearl(b5.getType())) {
                        event = new PlayerCrossPearlEvent(player, location, location.clone().add(-0.3D, 0.0D, -0.3D));
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                           location.setZ(location.getZ() - 0.3D);
                           location.setX(location.getX() - 0.3D);
                           this.crosspearl = true;
                           this.isEdited = true;
                           return b5;
                        }
                     }

                     return bb.getRelative(BlockFace.NORTH_WEST);
                  }
               } else if (direction.equals("SW")) {
                  if (this.isCannable(b6, location)) {
                     if (!Utils.thruEnabled(bb.getRelative(BlockFace.SOUTH_WEST).getType()) && Utils.crossPearl(b6.getType())) {
                        event = new PlayerCrossPearlEvent(player, location, location.clone().add(-0.3D, 0.0D, 0.3D));
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                           location.setZ(location.getZ() + 0.3D);
                           location.setX(location.getX() - 0.3D);
                           this.crosspearl = true;
                           this.isEdited = true;
                           return b6;
                        }
                     }

                     return bb.getRelative(BlockFace.SOUTH_WEST);
                  }

                  if (this.isCannable(b3, location)) {
                     if (!Utils.thruEnabled(bb.getRelative(BlockFace.SOUTH_WEST).getType()) && Utils.crossPearl(b3.getType())) {
                        event = new PlayerCrossPearlEvent(player, location, location.clone().add(-0.3D, 0.0D, 0.3D));
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                           location.setZ(location.getZ() + 0.3D);
                           location.setX(location.getX() - 0.3D);
                           this.crosspearl = true;
                           this.isEdited = true;
                           return b3;
                        }
                     }

                     return bb.getRelative(BlockFace.SOUTH_WEST);
                  }
               }

               if (EnderPearlSettings.PRE_THRU_BLOCK) {
                  switch(direction.hashCode()) {
                  case 69:
                     if (direction.equals("E") && Utils.thruEnabled(typee) && this.distance(b4, location, 0.8D)) {
                        if (!this.isStairGood(b4, direction)) {
                           return null;
                        }

                        location.setX(location.getX() + 0.3D);
                        return b4;
                     }
                     break;
                  case 78:
                     if (direction.equals("N") && Utils.thruEnabled(type3) && this.distance(b5, location, 0.8D)) {
                        if (!this.isStairGood(b5, direction)) {
                           return null;
                        }

                        location.setZ(location.getZ() - 0.3D);
                        return b5;
                     }
                     break;
                  case 83:
                     if (direction.equals("S") && Utils.thruEnabled(type4) && this.distance(b6, location, 0.8D)) {
                        if (!this.isStairGood(b6, direction)) {
                           return null;
                        }

                        location.setZ(location.getZ() + 0.3D);
                        return b6;
                     }
                     break;
                  case 87:
                     if (direction.equals("W") && Utils.thruEnabled(type2) && this.distance(b3, location, 0.8D)) {
                        if (!this.isStairGood(b3, direction)) {
                           return null;
                        }

                        location.setX(location.getX() - 0.3D);
                        return b3;
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   protected boolean checkRisky(Location location, Player player, MovingObjectPosition position) {
      Block b1 = location.getBlock();
      Block b2 = b1.getRelative(BlockFace.WEST);
      Block b3 = b1.getRelative(BlockFace.EAST);
      Block b4 = b1.getRelative(BlockFace.NORTH);
      Block b5 = b1.getRelative(BlockFace.SOUTH);
      if (!Utils.customTransparent(b1) && Utils.trappingMontage(b1.getRelative(BlockFace.DOWN)) && Utils.trappingMontage(b1.getRelative(BlockFace.UP)) && position.entity == null) {
         String s = Utils.direction(player.getLocation());
         if (s.contains("E") && !Utils.customTransparent(b3) && !b3.getType().toString().contains("CHEST") && !Utils.customTransparent(b3.getRelative(BlockFace.UP))) {
            return true;
         } else if (s.contains("W") && !Utils.customTransparent(b2) && !b2.getType().toString().contains("CHEST") && !Utils.customTransparent(b2.getRelative(BlockFace.UP))) {
            return true;
         } else if (s.contains("N") && !Utils.customTransparent(b4) && !b4.getType().toString().contains("CHEST") && !Utils.customTransparent(b4.getRelative(BlockFace.UP))) {
            return true;
         } else {
            return s.contains("S") && !Utils.customTransparent(b5) && !b5.getType().toString().contains("CHEST") && !Utils.customTransparent(b5.getRelative(BlockFace.UP));
         }
      } else {
         return true;
      }
   }

   public VEntityEnderPearl17(World world, EntityLiving entity) {
      super(world, entity);
      this.Blocked = Sets.immutableEnumSet(Material.THIN_GLASS, Material.IRON_FENCE, Material.FENCE);
      this.c = entity;
      this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls;
   }

   private boolean isStairGood(Block block, String s) {
      boolean b1 = Utils.stairs(block.getType());
      BlockFace face = null;
      if (b1) {
         Stairs stair = (Stairs)block.getState().getData();
         face = stair.getFacing();
      }

      if (b1 && face != null) {
         if (s.equals("E") && (face == BlockFace.WEST || face == BlockFace.EAST)) {
            return false;
         }

         if (s.equals("W") && (face == BlockFace.WEST || face == BlockFace.EAST)) {
            return false;
         }

         if (s.equals("N") && (face == BlockFace.NORTH || face == BlockFace.SOUTH)) {
            return false;
         }

         if (s.equals("S") && (face == BlockFace.NORTH || face == BlockFace.SOUTH)) {
            return false;
         }
      }

      return true;
   }

   private boolean distance(Block block, Location location, double d) {
      return block.getLocation().clone().add(0.5D, 0.0D, 0.5D).distance(location) <= d;
   }

   private boolean isCannable(Block block, Location location) {
      return this.distance(block, location, 1.0D) && Utils.thruEnabled(block.getType());
   }

   public VEntityEnderPearl17(World world) {
      super(world);
      this.Blocked = Sets.immutableEnumSet(Material.THIN_GLASS, Material.IRON_FENCE, Material.FENCE);
      this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls;
   }

   protected boolean checkFenceGate(Location location, Player player) {
      Block block = location.getBlock();
      Block b2 = block.getRelative(BlockFace.DOWN);
      Material type = block.getType();
      if (type.toString().contains("FENCE_GATE")) {
         if (b2.getType().toString().contains("CHEST") && EnderPearlSettings.ANTIGLITCH_CHEST_FENCE) {
            return true;
         } else if (b2.getType() == Material.HOPPER && EnderPearlSettings.ANTIGLITCH_HOPPER_FENCE) {
            return true;
         } else if (!((Gate)block.getState().getData()).isOpen()) {
            return b2.getType().toString().contains("FENCE_GATE") && ((Gate)b2.getState().getData()).isOpen();
         } else {
            this.isEdited = true;
            this.setToBlock(location);
            return true;
         }
      } else {
         return true;
      }
   }

   private void setToBlock(Location location) {
      location.setX((double)location.getBlockX() + 0.5D);
      location.setY(location.getBlockY());
      location.setZ((double)location.getBlockZ() + 0.5D);
   }
}
