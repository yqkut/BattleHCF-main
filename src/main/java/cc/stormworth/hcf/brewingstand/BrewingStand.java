package cc.stormworth.hcf.brewingstand;

import cc.stormworth.core.CorePlugin;
import cc.stormworth.core.kt.util.ItemBuilder;
import cc.stormworth.hcf.Main;
import cc.stormworth.hcf.util.InventoryUtil;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter @Setter
@RequiredArgsConstructor
public class BrewingStand {

    private final Location location;
    private boolean active = true;
    private PotionCategory activeCategory;
    private Status status = Status.NOT_ACTIVE;

    private org.bukkit.block.BrewingStand brewingStand;

    private int secondsBrewing = 7;

    private final Map<Material, List<ItemStack>> resources = Maps.newHashMap();

    private int currentPotionsCount = 0;
    private int totalPotions = 0;

    public void openInventoryOf(Player player, Material material){
        Inventory inventory = Bukkit.createInventory(null, 9 * 3, "Resources");

        inventory.setItem(0, new ItemBuilder(material).addToLore("").build());

        inventory.setItem(InventoryUtil.getSlot(0, 1), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(8).name(" ").build());

        for (int i = 0; i < 3; i++) {
            inventory.setItem(InventoryUtil.getSlot(1, i), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(8).name(" ").build());
        }

        inventory.setItem(InventoryUtil.getSlot(0, 2), new ItemBuilder(Material.BED).name("&cGo back").build());

        LinkedList<ItemStack> items = new LinkedList<>(resources.getOrDefault(material, new CopyOnWriteArrayList<>()));

        if (!items.isEmpty()){
            for (int i = 0; i < 27; i++) {
                if (inventory.getItem(i) == null){
                    inventory.setItem(i, items.poll());
                }
            }
        }

        player.openInventory(inventory);
    }

    public int getAmountOf(Material material){

        int amount = 0;

        List<ItemStack> resources = this.resources.get(material);

        if (resources != null){
            for (ItemStack resource : resources) {
                if (resource != null && resource.getAmount() > 0){
                    amount += resource.getAmount();
                }
            }
        }

        return amount;
    }

    public int checkAmountOfBottles(){

        if (resources.containsKey(Material.POTION)){

            if (getAmountOf(Material.POTION) >= 37){
                return getAmountOf(Material.POTION);
            }
        }else{
            resources.put(Material.POTION, new CopyOnWriteArrayList<>());
        }

        int amount = getAmountOf(Material.POTION);

        Block blockUp = location.getBlock().getRelative(0, 1, 0);

        if (blockUp.getType() == Material.HOPPER){
            Hopper hopper = (Hopper) blockUp.getState();

            for (int i = 0; i < hopper.getInventory().getSize(); i++) {
                ItemStack item = hopper.getInventory().getItem(i);

                if (item != null && item.getType() == Material.POTION && item.getDurability() == 0){
                    resources.get(Material.POTION).add(item);
                    amount += item.getAmount();

                    hopper.getInventory().setItem(i, null);

                    hopper.update();

                    totalPotions =+ item.getAmount();

                    if (getAmountOf(Material.POTION) >= 37){
                        break;
                    }
                }
            }
        }

        return amount;
    }

    public void calculatePotions(){
        if (activeCategory == null){
            totalPotions = 0;
            currentPotionsCount = 0;
            return;
        }

        int amount = 0;

        for (Material material : activeCategory.getResources()){
            if (amount > 0){
                amount = Math.min(amount, getAmountOf(material));
            }else {
                amount = getAmountOf(material);
            }
        }

        int amountOfBottles = getAmountOf(Material.POTION);

        totalPotions = Math.min(amountOfBottles, amount * 3);
    }

    public boolean hasEnoughResources(){

        if (checkAmountOfBottles() < 3){
            return false;
        }

        if (activeCategory == null){
            return false;
        }

        for (Material material : activeCategory.getResources()){
            if (getAmountOf(material) <= 0){
                return false;
            }
        }

        return true;
    }

    public void tick(){
        if (active){
            if (activeCategory != null && status == Status.FULL_INVENTORY){
                Block blockBelow = location.getBlock().getRelative(0, -1, 0);

                if (blockBelow.getType() == Material.HOPPER){
                    Hopper hopper = (Hopper) blockBelow.getState();

                    if (hopper.getInventory().firstEmpty() != -1){
                        calculatePotions();
                        status = Status.BREWING;
                        currentPotionsCount = 0;
                        secondsBrewing = 7;
                    }
                }
            } else if (activeCategory != null && status != Status.BREWING){
                if (hasEnoughResources()){
                    calculatePotions();
                    status = Status.BREWING;
                    currentPotionsCount = 0;
                    secondsBrewing = 7;
                } else {
                    status = Status.NOT_RESOURCES;
                }
            }else if (status == Status.BREWING){

                if (activeCategory == null){
                    status = Status.NOT_ACTIVE;
                    return;
                }

                checkAmountOfBottles();

                Block blockBelow = location.getBlock().getRelative(0, -1, 0);

                if (blockBelow.getType() == Material.HOPPER){
                    Hopper hopper = (Hopper) blockBelow.getState();

                    if (hopper.getInventory().firstEmpty() == -1){
                        status = Status.FULL_INVENTORY;
                        return;
                    }
                }else{
                    status = Status.FULL_INVENTORY;
                    return;
                }

                secondsBrewing--;

                //Bukkit.getWorld("world").playSound(location, Sound.NOTE_PLING, 1, 1);

                if (secondsBrewing <= 0) {

                    for (Material material : activeCategory.getResources()){
                        List<ItemStack> resources = this.resources.get(material);

                        if (resources != null){
                            for (ItemStack resource : resources) {
                                if (resource != null && resource.getAmount() > 0){
                                    if (resource.getAmount() == 1){
                                        resources.remove(resource);
                                    } else {
                                        resource.setAmount(resource.getAmount() - 1);
                                    }
                                }
                            }
                        }
                    }

                    List<ItemStack> bottles = this.resources.get(Material.POTION);

                    if (bottles != null && !bottles.isEmpty() && bottles.size() >= 3){
                        for (int i = 0; i < 3; i++) {
                            bottles.remove(0);
                        }
                    }else {
                        status = Status.NOT_RESOURCES;
                        return;
                    }

                    for (int i = 0; i < 3; i++) {
                        brewingStand.getInventory().addItem(activeCategory.getResult());
                    }

                    brewingStand.update();

                    currentPotionsCount += 3;

                    if (!hasEnoughResources()) {
                        status = Status.NOT_RESOURCES;
                    }

                    secondsBrewing = 7;
                }
            }
        }
    }

    public void destroy() {
        active = false;
        status = Status.NOT_ACTIVE;
        activeCategory = null;
        resources.clear();
        totalPotions = 0;
        currentPotionsCount = 0;
        secondsBrewing = 7;

        Block stand = location.getBlock();

        if (stand.getType() == Material.BREWING_STAND){
            stand.setType(Material.AIR);
        }

        Main.getInstance().getBrewingStandManager().removeBrewingStand(this);
    }

    @RequiredArgsConstructor
    @Getter
    public enum Status {
        BREWING("&eBrewing."),
        NOT_RESOURCES("&cNot resources."),
        FINISHED("&aFinished."),
        NOT_ACTIVE("&cNot active."),
        FULL_INVENTORY("&cFull inventory.");

        private final String name;
    }

    public BrewingStand(Document document){
        this.location = CorePlugin.GSON.fromJson(document.getString("location"), Location.class);
        this.active = document.getBoolean("active");
        if (document.containsKey("activeCategory") && document.getString("activeCategory") != null) {
            this.activeCategory = PotionCategory.valueOf(document.getString("activeCategory"));
        }
        this.status = Status.valueOf(document.getString("status"));

        if (location.getBlock() != null && location.getBlock().getType() == Material.BREWING_STAND){
            this.brewingStand = (org.bukkit.block.BrewingStand) location.getBlock().getState();
        }else{
            return;
        }

        Document resources = document.get("resources", Document.class);

        for (String key : resources.keySet()) {
            Material material = Material.valueOf(key);
            List<ItemStack> items = new CopyOnWriteArrayList<>();

            for (Document item : resources.getList(key, Document.class)) {
                items.add(CorePlugin.GSON.fromJson(item.toJson(), ItemStack.class));
            }

            this.resources.put(material, items);
        }

        calculatePotions();
    }

    public Document toDocument(){
        Document document = new Document();

        document.put("location", CorePlugin.GSON.toJson(location));
        document.put("active", active);

        if (activeCategory != null){
            document.put("activeCategory", activeCategory.name());
        }

        document.put("status", status.name());

        Document resources = new Document();

        for (Map.Entry<Material, List<ItemStack>> entry : this.resources.entrySet()) {
            List<Document> items = new ArrayList<>();

            for (ItemStack item : entry.getValue()) {
                items.add(Document.parse(CorePlugin.GSON.toJson(item)));
            }

            resources.put(entry.getKey().name(), items);
        }

        document.put("resources", resources);
        return document;
    }
}
