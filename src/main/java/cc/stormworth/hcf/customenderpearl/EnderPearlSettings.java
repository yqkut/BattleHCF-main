package cc.stormworth.hcf.customenderpearl;

import cc.stormworth.core.file.ConfigFile;
import cc.stormworth.hcf.Main;
import com.google.common.collect.Lists;
import org.bukkit.configuration.Configuration;

import java.util.List;
public class EnderPearlSettings {

    public static boolean ON_OPEN_FENCE_GATE_LAUNCH = false;
    public static  List<String> onClickAutoLaunch = Lists.newArrayList(
            "FURNACE",
            "ANVIL",
            "SIGN",
            "SIGN_POST",
            "WALL_SIGN",
            "CHEST",
            "ENDER_CHEST",
            "TRAPPED_CHEST",
            "WORKBENCH",
            "ENCHANTMENT_TABLE",
            "BEACON",
            "ANVIL"
    );

    public static  boolean THRU_SLABS = true;
    public static  boolean THRU_STAIRS = true;
    public static  boolean THRU_COBBLE_WALLS = true;
    public static  boolean THRU_BED = true;
    public static  boolean THRU_PORTAL_FRAME = true;
    public static  boolean THRU_ENCHANT_TABLE = true;
    public static  boolean THRU_CHESTS = true;
    public static  boolean THRU_ANVIL = true;
    public static  boolean THRU_DAY_LIGHT_SENSOR = true;
    public static  boolean THRU_TRAPDOOR = true;
    public static  boolean THRU_PISTON = true;

    public static  boolean THRU_SLABS_CROSSPEARL = true;
    public static  boolean THRU_STAIRS_CROSSPEARL = true;
    public static  boolean THRU_PISTONS_CROSSPEARL = true;
    public static  boolean THRU_ANVIL_CROSSPEARL = true;
    public static  boolean THRU_PORTAL_FRAME_CROSSPEARL = true;
    public static  boolean THRU_ENCHANT_TABLE_CROSSPEARL = true;
    public static  boolean THRU_BED_CROSSPEARL = true;
    public static  boolean THRU_CHESTS_CROSSPEARL = true;
    public static  boolean THRU_COBBLE_WALL_CROSSPEARL = true;
    public static  boolean THRU_DAY_LIGHT_SENSOR_CROSSPEARL = true;
    public static  boolean THRU_TRAP_DOOR_CROSSPEARL = true;


    public static  boolean THRU_SLABS_DIAGONAL = true;
    public static  boolean THRU_STAIRS_HORIZONTAL = true;
    public static  boolean THRU_PISTONS_HORIZONTAL = true;
    public static  boolean THRU_ANVIL_HORIZONTAL = true;
    public static  boolean THRU_PORTAL_FRAME_HORIZONTAL = true;
    public static  boolean THRU_ENCHANT_TABLE_HORIZONTAL = true;
    public static  boolean THRU_BED_HORIZONTAL = true;
    public static  boolean THRU_CHESTS_HORIZONTAL = true;
    public static  boolean THRU_COBBLE_WALL_HORIZONTAL = true;
    public static  boolean THRU_DAY_LIGHT_SENSOR_HORIZONTAL = true;
    public static  boolean THRU_TRAP_DOOR_HORIZONTAL = true;

    public static  boolean THRU_SLABS_CRITBLOCK = true;
    public static  boolean THRU_STAIRS_CRITBLOCK = true;
    public static  boolean THRU_PISTONS_CRITBLOCK = true;
    public static  boolean THRU_ANVIL_CRITBLOCK = true;
    public static  boolean THRU_PORTAL_FRAME_CRITBLOCK = true;
    public static  boolean THRU_ENCHANT_TABLE_CRITBLOCK = true;
    public static  boolean THRU_BED_CRITBLOCK = true;
    public static  boolean THRU_CHESTS_CRITBLOCK = true;
    public static  boolean THRU_COBBLE_WALL_CRITBLOCK = true;
    public static  boolean THRU_DAY_LIGHT_SENSOR_CRITBLOCK = true;
    public static  boolean THRU_TRAP_DOOR_CRITBLOCK = true;

    public static  boolean PEARL_TAIL_TELEPORT = true;
    public static  double PEARL_TAIL_TELEPORT_Y = 1.0;
    public static  double PEARL_TAIL_TELEPORT_Z = 0.5;
    public static  int PEARL_MAX_PEARL_PASS_THRU_BLOCKS = 1;

    public static  boolean THRU_FENCES = true;
    public static  boolean THRU_COWEB = true;
    public static  boolean THRU_STRING = true;
    public static  double PEARL_DAMAGE = 5.0;
    public static  boolean PEARL_PARTICLES = true;
    public static  int PEARL_PARTICLES_AMOUNT = 32;

    public static  boolean REFUND_RISKY_PEARL = true;
    public static  boolean REFUND_IF_SO_CLOSE = true;
    public static  boolean REFUND_PEARL_IF_SUFFOCATING = true;
    public static  boolean FIX_WALLS_GLITCH = true;
    public static  boolean FIX_FENCE_GATE_GLITCH = true;

    public static  boolean BETTER_HIT_DETECTION = true;
    public static  boolean GET_OUT_FROM_ONE_BY_ONE = true;

    public static  double REFUND_IF_SO_CLOSE_DISTANCE = 1.0;

    public static  boolean HIT_THRU_BLOCK = true;
    public static  boolean PRE_THRU_BLOCK = true;

    public static  boolean ANTIGLITCH_CHEST_FENCE = true;
    public static  boolean ANTIGLITCH_HOPPER_FENCE = false;

    public EnderPearlSettings(ConfigFile file){
        loadConfig(file);
    }

    public void loadConfig(ConfigFile file){
        Configuration config = file.getConfig();

        ON_OPEN_FENCE_GATE_LAUNCH = config.getBoolean("onOpenFenceGateLaunch", false);
        onClickAutoLaunch = config.getStringList("onClickAutoLaunch");
        THRU_SLABS = config.getBoolean("thruSlabs", true);
        THRU_STAIRS = config.getBoolean("thruStairs", true);
        THRU_COBBLE_WALLS = config.getBoolean("thruCobbleWalls", true);
        THRU_BED = config.getBoolean("thruBed", true);
        THRU_PORTAL_FRAME = config.getBoolean("thruPortalFrame", true);
        THRU_ENCHANT_TABLE = config.getBoolean("thruEnchantTable", true);
        THRU_CHESTS = config.getBoolean("thruChests", true);
        THRU_ANVIL = config.getBoolean("thruAnvil", true);
        THRU_DAY_LIGHT_SENSOR = config.getBoolean("thruDayLightSensor", true);
        THRU_TRAPDOOR = config.getBoolean("thruTrapDoor", true);
        THRU_PISTON = config.getBoolean("thruPiston", true);
        THRU_SLABS_CROSSPEARL = config.getBoolean("thruSlabsCrossPearl", true);
        THRU_STAIRS_CROSSPEARL = config.getBoolean("thruStairsCrossPearl", true);
        THRU_PISTONS_CROSSPEARL = config.getBoolean("thruPistonsCrossPearl", true);
        THRU_ANVIL_CROSSPEARL = config.getBoolean("thruAnvilCrossPearl", true);
        THRU_PORTAL_FRAME_CROSSPEARL = config.getBoolean("thruPortalFrameCrossPearl", true);
        THRU_ENCHANT_TABLE_CROSSPEARL = config.getBoolean("thruEnchantTableCrossPearl", true);
        THRU_BED_CROSSPEARL = config.getBoolean("thruBedCrossPearl", true);
        THRU_CHESTS_CROSSPEARL = config.getBoolean("thruChestsCrossPearl", true);
        THRU_COBBLE_WALL_CROSSPEARL = config.getBoolean("thruCobbleWallCrossPearl", true);
        THRU_DAY_LIGHT_SENSOR_CROSSPEARL = config.getBoolean("thruDayLightSensorCrossPearl", true);
        THRU_TRAP_DOOR_CROSSPEARL = config.getBoolean("thruTrapDoorCrossPearl", true);
        THRU_SLABS_DIAGONAL = config.getBoolean("thruSlabsDiagonal", true);
        THRU_STAIRS_HORIZONTAL = config.getBoolean("thruStairsHorizontal", true);
        THRU_PISTONS_HORIZONTAL = config.getBoolean("thruPistonsHorizontal", true);
        THRU_ANVIL_HORIZONTAL = config.getBoolean("thruAnvilHorizontal", true);
        THRU_PORTAL_FRAME_HORIZONTAL = config.getBoolean("thruPortalFrameHorizontal", true);
        THRU_ENCHANT_TABLE_HORIZONTAL = config.getBoolean("thruEnchantTableHorizontal", true);
        THRU_BED_HORIZONTAL = config.getBoolean("thruBedHorizontal", true);
        THRU_CHESTS_HORIZONTAL = config.getBoolean("thruChestsHorizontal", true);
        THRU_COBBLE_WALL_HORIZONTAL = config.getBoolean("thruCobbleWallHorizontal", true);
        THRU_DAY_LIGHT_SENSOR_HORIZONTAL = config.getBoolean("thruDayLightSensorHorizontal", true);
        THRU_TRAP_DOOR_HORIZONTAL = config.getBoolean("thruTrapDoorHorizontal", true);

        THRU_FENCES = config.getBoolean("thruFences", true);
        THRU_COWEB = config.getBoolean("thruCobweb", true);
        THRU_STRING = config.getBoolean("thruString", true);
        PEARL_DAMAGE = config.getDouble("pearlDamage", 5.0);
        PEARL_PARTICLES = config.getBoolean("pearlParticles", true);
        PEARL_PARTICLES_AMOUNT = config.getInt("pearlParticlesAmount", 32);

        REFUND_RISKY_PEARL = config.getBoolean("refundRiskyPearl", true);
        REFUND_IF_SO_CLOSE = config.getBoolean("refundIfSoClose", true);
        REFUND_PEARL_IF_SUFFOCATING = config.getBoolean("refundPearlIfSuffocating", true);
        FIX_WALLS_GLITCH = config.getBoolean("fixWallsGlitch", true);
        FIX_FENCE_GATE_GLITCH = config.getBoolean("fixFenceGateGlitch", true);

        BETTER_HIT_DETECTION = config.getBoolean("betterHitDetection", true);
        GET_OUT_FROM_ONE_BY_ONE = config.getBoolean("getOutFromOneByOne", true);

        REFUND_IF_SO_CLOSE_DISTANCE = config.getDouble("refundIfSoCloseDistance", 1.0);

        HIT_THRU_BLOCK = config.getBoolean("hitThruBlock", true);
        PRE_THRU_BLOCK = config.getBoolean("preThruBlock", true);

        ANTIGLITCH_CHEST_FENCE = config.getBoolean("antiglitchChestFence", true);
        ANTIGLITCH_HOPPER_FENCE = config.getBoolean("antiglitchHopperFence", false);

        file.save();
    }

    public void reloadConfig(){
        Main.getInstance().setEnderPearlConfig(new ConfigFile(Main.getInstance(), "enderpearls.yml"));

        ConfigFile file = Main.getInstance().getEnderPearlConfig();

        loadConfig(file);

        file.save();
    }

}
