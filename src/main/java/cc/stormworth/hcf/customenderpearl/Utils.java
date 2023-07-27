package cc.stormworth.hcf.customenderpearl;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

@UtilityClass
public class Utils {


   public boolean thruEnabled(Material material) {
      String s = material.toString();
      if (slab(material)) {
         return EnderPearlSettings.THRU_SLABS;
      } else if (stairs(material)) {
         return EnderPearlSettings.THRU_STAIRS;
      } else if (s.contains("WALL")) {
         return EnderPearlSettings.THRU_COBBLE_WALLS;
      } else if (s.contains("BED")) {
         return EnderPearlSettings.THRU_BED;
      } else if (material != Material.PISTON_EXTENSION && material != Material.PISTON_MOVING_PIECE) {
         if (material == Material.ENDER_PORTAL_FRAME) {
            return EnderPearlSettings.THRU_PORTAL_FRAME;
         } else if (material == Material.ENCHANTMENT_TABLE) {
            return EnderPearlSettings.THRU_ENCHANT_TABLE;
         } else if (s.contains("CHEST")) {
            return EnderPearlSettings.THRU_CHESTS;
         } else if (s.contains("ANVIL")) {
            return EnderPearlSettings.THRU_ANVIL;
         } else if (s.contains("DAYLIGHT")) {
            return EnderPearlSettings.THRU_DAY_LIGHT_SENSOR;
         } else {
            return (s.equals("IRON_TRAPDOOR") || material == Material.TRAP_DOOR) && EnderPearlSettings.THRU_TRAPDOOR;
         }
      } else {
         return EnderPearlSettings.THRU_PISTON;
      }
   }


   public boolean crossPearl(Material material) {
      if (thruEnabled(material)) {
         String s = material.toString();
         if (slab(material)) {
            return EnderPearlSettings.THRU_SLABS_CROSSPEARL;
         }

         if (stairs(material)) {
            return EnderPearlSettings.THRU_STAIRS_CROSSPEARL;
         }

         if (material == Material.PISTON_EXTENSION || material == Material.PISTON_MOVING_PIECE) {
            return EnderPearlSettings.THRU_PISTONS_CROSSPEARL;
         }

         if (s.contains("ANVIL")) {
            return EnderPearlSettings.THRU_ANVIL_CROSSPEARL;
         }

         if (material == Material.ENDER_PORTAL_FRAME) {
            return EnderPearlSettings.THRU_PORTAL_FRAME_CROSSPEARL;
         }

         if (material == Material.ENCHANTMENT_TABLE) {
            return EnderPearlSettings.THRU_ENCHANT_TABLE_CROSSPEARL;
         }

         if (s.contains("BED")) {
            return EnderPearlSettings.THRU_BED_CROSSPEARL;
         }

         if (s.contains("CHEST")) {
            return EnderPearlSettings.THRU_CHESTS_CROSSPEARL;
         }

         if (s.contains("WALL")) {
            return EnderPearlSettings.THRU_COBBLE_WALL_CROSSPEARL;
         }

         if (s.contains("DAYLIGHT")) {
            return EnderPearlSettings.THRU_DAY_LIGHT_SENSOR_CROSSPEARL;
         }

         if (s.equals("IRON_TRAPDOOR") || material == Material.TRAP_DOOR) {
            return EnderPearlSettings.THRU_TRAP_DOOR_CROSSPEARL;
         }
      }

      return false;
   }

   public static boolean trappingMontage(Block block) {
      Material material = block.getType();
      String s = material.toString();
      if (!s.contains("RAIL") && material != Material.RAILS) {
         if (!s.contains("FLOWER") && !s.contains("ROSE") && material != Material.DOUBLE_PLANT && material != Material.LONG_GRASS) {
            if (s.contains("SAPLING")) {
               return false;
            } else if (s.contains("MUSHROOM")) {
               return false;
            } else if (s.contains("TORCH")) {
               return false;
            } else if (!s.contains("SNOW") && !s.contains("CARPET") && material != Material.LEVER) {
               if (s.contains("REDSTONE")) {
                  return false;
               } else if (s.contains("PORTAL")) {
                  return false;
               } else if (s.contains("SUGAR_CANE")) {
                  return false;
               } else if (!s.contains("STEM") && material != Material.CROPS && material != Material.NETHER_WARTS && material != Material.COCOA) {
                  if (!s.contains("BUTTON") && !s.contains("WALL") && !s.contains("TRIPWIRE") && material != Material.FIRE) {
                     if (!s.contains("SIGN") && !s.contains("DOOR") && !s.contains("PLATE")) {
                        if (material != Material.PISTON_EXTENSION && material != Material.PISTON_MOVING_PIECE) {
                           if (!s.contains("PLATE") && material != Material.WEB) {
                              if (material == Material.LADDER) {
                                 return false;
                              } else if (material == Material.AIR) {
                                 return false;
                              } else if (s.contains("FENCE_GATE")) {
                                 return false;
                              } else {
                                 return !block.isLiquid();
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static double yaw(Location location) {
      double ya = (double)((location.getYaw() - 90.0F) % 360.0F);
      if (ya < 0.0D) {
         ya += 360.0D;
      }

      return ya;
   }

   public static boolean customTransparent(Block block) {
      Material type = block.getType();
      String s = type.toString();

      if (!s.contains("RAIL") && type != Material.RAILS && !s.contains("ANVIL")) {
         if (!s.contains("FLOWER") && !s.contains("ROSE") && type != Material.DOUBLE_PLANT && type != Material.LONG_GRASS) {
            if (s.contains("SAPLING")) {
               return false;
            } else if (s.contains("MUSHROOM")) {
               return false;
            } else if (s.contains("TORCH")) {
               return false;
            } else if (!s.contains("SNOW") && !s.contains("CARPET") && type != Material.LEVER && type != Material.BREWING_STAND) {
               if (!s.contains("REDSTONE") && !s.contains("REDSTONE_")) {
                  if (s.contains("PORTAL")) {
                     return false;
                  } else if (s.contains("TRAPDOOR")) {
                     return false;
                  } else if (s.contains("SUGAR_CANE")) {
                     return false;
                  } else if (!s.contains("STEM") && !s.contains("DAYLIGHT") && type != Material.CROPS && type != Material.NETHER_WARTS && type != Material.COCOA) {
                     if (!s.contains("BUTTON") && !s.contains("TRIPWIRE") && type != Material.FIRE) {
                        if (!s.contains("SIGN") && type != Material.TRAP_DOOR && !s.contains("DOOR") && !s.contains("PLATE")) {
                           if (type != Material.PISTON_EXTENSION && type != Material.PISTON_MOVING_PIECE) {
                              if (!s.contains("CHEST") && !s.contains("PLATE") && type != Material.WEB) {
                                 if (type == Material.LADDER) {
                                    return false;
                                 } else if (type == Material.AIR) {
                                    return false;
                                 } else if (type != Material.ENDER_PORTAL_FRAME && type != Material.ENCHANTMENT_TABLE && !s.contains("FENCE_GATE")) {
                                    if (!slab(type) && !stairs(type) && !s.contains("WALL") && !s.contains("BED")) {
                                       return !block.isLiquid();
                                    } else {
                                       return false;
                                    }
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static String direction(Location location) {
      double d = (location.getYaw() - 90.0F) % 360.0F;
      if (d < 0.0D) {
         d += 360.0D;
      }

      if (0.0D <= d && d < 22.5D) {
         return "W";
      } else if (22.5D <= d && d < 67.5D) {
         return "NW";
      } else if (67.5D <= d && d < 112.5D) {
         return "N";
      } else if (112.5D <= d && d < 157.5D) {
         return "NE";
      } else if (157.5D <= d && d < 202.5D) {
         return "E";
      } else if (202.5D <= d && d < 247.5D) {
         return "SE";
      } else if (247.5D <= d && d < 292.5D) {
         return "S";
      } else if (292.5D <= d && d < 337.5D) {
         return "SW";
      } else {
         return 337.5D <= d && d < 360.0D ? "W" : "";
      }
   }

   public boolean diagonalPearl(Material material) {
      String s = material.toString();
      if (thruEnabled(material)) {
         if (slab(material)) {
            return EnderPearlSettings.THRU_SLABS_DIAGONAL;
         }

         if (stairs(material)) {
            return EnderPearlSettings.THRU_STAIRS_HORIZONTAL;
         }

         if (material == Material.PISTON_EXTENSION || material == Material.PISTON_MOVING_PIECE) {
            return EnderPearlSettings.THRU_PISTONS_HORIZONTAL;
         }

         if (s.contains("ANVIL")) {
            return EnderPearlSettings.THRU_ANVIL_HORIZONTAL;
         }

         if (material == Material.ENDER_PORTAL_FRAME) {
            return EnderPearlSettings.THRU_PORTAL_FRAME_HORIZONTAL;
         }

         if (material == Material.ENCHANTMENT_TABLE) {
            return EnderPearlSettings.THRU_ENCHANT_TABLE_HORIZONTAL;
         }

         if (s.contains("BED")) {
            return EnderPearlSettings.THRU_BED_HORIZONTAL;
         }

         if (s.contains("CHEST")) {
            return EnderPearlSettings.THRU_CHESTS_HORIZONTAL;
         }

         if (s.contains("WALL")) {
            return EnderPearlSettings.THRU_COBBLE_WALL_HORIZONTAL;
         }

         if (s.contains("DAYLIGHT")) {
            return EnderPearlSettings.THRU_DAY_LIGHT_SENSOR_HORIZONTAL;
         }

         if (s.equals("IRON_TRAPDOOR") || material == Material.TRAP_DOOR) {
            return EnderPearlSettings.THRU_TRAP_DOOR_HORIZONTAL;
         }
      }

      return false;
   }

   public static BlockFace getBehind(String s) {
      switch(s.hashCode()) {
      case 69:
         if (s.equals("E")) {
            return BlockFace.WEST;
         }
         break;
      case 78:
         if (s.equals("N")) {
            return BlockFace.SOUTH;
         }
         break;
      case 83:
         if (s.equals("S")) {
            return BlockFace.NORTH;
         }
         break;
      case 87:
         if (s.equals("W")) {
            return BlockFace.EAST;
         }
      }

      return null;
   }


   public boolean critblock(Material material) {
      if (thruEnabled(material)) {
         String name = material.toString();
         if (slab(material)) {
            return EnderPearlSettings.THRU_SLABS_CRITBLOCK;
         }
         if (stairs(material)) {
            return EnderPearlSettings.THRU_STAIRS_CRITBLOCK;
         }
         if (material == Material.PISTON_EXTENSION || material == Material.PISTON_MOVING_PIECE) {
            return EnderPearlSettings.THRU_PISTONS_CRITBLOCK;
         }
         if (name.contains("ANVIL")) {
            return EnderPearlSettings.THRU_ANVIL_CRITBLOCK;
         }
         if (material == Material.ENDER_PORTAL_FRAME) {
            return EnderPearlSettings.THRU_PORTAL_FRAME_CRITBLOCK;
         }
         if (material == Material.ENCHANTMENT_TABLE) {
            return EnderPearlSettings.THRU_ENCHANT_TABLE_CRITBLOCK;
         }
         if (name.contains("BED")) {
            return EnderPearlSettings.THRU_BED_CRITBLOCK;
         }
         if (name.contains("CHEST")) {
            return EnderPearlSettings.THRU_CHESTS_CRITBLOCK;
         }
         if (name.contains("WALL")) {
            return EnderPearlSettings.THRU_COBBLE_WALL_CRITBLOCK;
         }
         if (name.contains("DAYLIGHT")) {
            return EnderPearlSettings.THRU_DAY_LIGHT_SENSOR_CRITBLOCK;
         }
         if (name.equals("IRON_TRAPDOOR") || material == Material.TRAP_DOOR) {
            return EnderPearlSettings.THRU_TRAP_DOOR_CRITBLOCK;
         }
      }

      return false;
   }

   public static boolean slab(Material material) {
      return (material.toString().contains("STEP") || material.name().equals("STONE_SLAB2")) && !material.toString().contains("DOUBLE");
   }

   public static boolean stairs(Material material) {
      return material.toString().contains("STAIRS");
   }
}
