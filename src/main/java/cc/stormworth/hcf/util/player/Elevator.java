package cc.stormworth.hcf.util.player;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;

public enum Elevator {
    UP,
    DOWN;

    public Location getCalculatedLocation(final Location location, final Type type) {
        if (this == Elevator.UP) {
            for (int yLevel = location.getBlockY(); yLevel < 256; ++yLevel) {
                if (location.getBlockY() != yLevel) {
                    final Location first = new Location(location.getWorld(), location.getX(), yLevel, location.getBlockZ());
                    final Location second = new Location(location.getWorld(), location.getX(), (yLevel + 1), location.getBlockZ());
                    if (type == Type.MINE_CART && this.isValidForMineCart(first, second)) {
                        return new Location(location.getWorld(), location.getBlockX(), yLevel, location.getBlockZ());
                    }
                    if (type == Type.CARPET && this.isValidForCarpet(this, first, second) == Cause.ALLOWED) {
                        return new Location(location.getWorld(), location.getBlockX(), yLevel, location.getBlockZ());
                    }
                    if (type == Type.SIGN && this.isValidForSign(this, first, second) == Cause.ALLOWED) {
                        return new Location(location.getWorld(), location.getBlockX(), yLevel, location.getBlockZ());
                    }
                }
            }
            return null;
        }
        for (int yLevel = location.getBlockY(); yLevel > 0; --yLevel) {
            if (location.getBlockY() != yLevel) {
                final Location first = new Location(location.getWorld(), location.getX(), yLevel, location.getBlockZ());
                final Location second = new Location(location.getWorld(), location.getX(), (yLevel + 1), location.getBlockZ());
                if (type == Type.MINE_CART && this.isValidForMineCart(first, second)) {
                    return first;
                }
                if (type == Type.CARPET && this.isValidForCarpet(Elevator.DOWN, first, second) == Cause.ALLOWED) {
                    return first;
                }
                if (type == Type.SIGN && this.isValidForSign(Elevator.DOWN, first, second) == Cause.ALLOWED) {
                    return first;
                }
            }
        }
        return null;
    }

    private boolean isValidForMineCart(final Location first, final Location second) {
        return (first.getBlock().getType() == Material.AIR && second.getBlock().getType() == Material.AIR) || (first.getBlock().getType() == Material.FENCE_GATE && second.getBlock().getType() == Material.FENCE_GATE);
    }

    private Cause isValidForCarpet(final Elevator direction, final Location first, final Location second) {
        if (second.getBlock().getType() != null && second.getBlock().getType() != Material.AIR) {
            return Cause.SECOND_BLOCK_NOT_AIR;
        }
        if (first.getBlock().getType() != Material.CARPET && first.getBlock().getType() == Material.AIR) {
            return Cause.ALLOWED;
        }
        if (first.getBlock().getType() != Material.CARPET) {
            return Cause.FIRST_BLOCK_NOT_REQUIRED_ITEM;
        }
        byte data = 0;
        if (direction == Elevator.UP) {
            data = 14;
        } else if (direction == Elevator.DOWN) {
            data = 13;
        }
        if (first.getBlock().getData() != data) {
            return Cause.INVALID_DIRECTION;
        }
        return Cause.ALLOWED;
    }

    private Cause isValidForSign(final Elevator elevator, final Location first, final Location second) {
        if (second.getBlock().getType() != null && second.getBlock().getType() != Material.AIR) {
            return Cause.SECOND_BLOCK_NOT_AIR;
        }
        if (!first.getBlock().getType().name().contains("SIGN") && first.getBlock().getType() == Material.AIR) {
            return Cause.ALLOWED;
        }
        if (!first.getBlock().getType().name().contains("SIGN")) {
            return Cause.FIRST_BLOCK_NOT_REQUIRED_ITEM;
        }
        final Sign sign = (Sign) first.getBlock().getState();
        if (!sign.getLine(0).contains("Elevator")) {
            return Cause.NOT_ELEVATOR;
        }
        if (elevator == Elevator.UP) {
            if (sign.getLine(1).equalsIgnoreCase("Down")) {
                return Cause.ALLOWED;
            }
        } else if (sign.getLine(1).equalsIgnoreCase("Up")) {
            return Cause.ALLOWED;
        }
        return Cause.INVALID_DIRECTION;
    }

    private enum Cause {
        FIRST_BLOCK_NOT_REQUIRED_ITEM,
        SECOND_BLOCK_NOT_AIR,
        NOT_ELEVATOR,
        INVALID_DIRECTION,
        ALLOWED
    }

    public enum Type {
        SIGN,
        CARPET,
        MINE_CART
    }
}