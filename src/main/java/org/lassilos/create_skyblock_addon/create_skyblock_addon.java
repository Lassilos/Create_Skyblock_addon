package org.lassilos.create_skyblock_addon;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(create_skyblock_addon.MODID)
public class create_skyblock_addon {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_skyblock_addon";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "create_skyblock_addon" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "create_skyblock_addon" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "create_skyblock_addon" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    // Creates a new Block with the id "create_skyblock_addon:example_block", combining the namespace and path
    //public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "create_skyblock_addon:example_block", combining the namespace and path
    //public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    public static final DeferredBlock<Block> STONE_CASING = BLOCKS.registerSimpleBlock("stone_casing");

    public static final DeferredItem<BlockItem> STONE_CASING_ITEM = ITEMS.registerSimpleBlockItem("stone_casing", STONE_CASING);

    // Creates a new food item with the id "create_skyblock_addon:example_id", nutrition 1 and saturation 2
    //public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    public static final DeferredItem<Item> BROKEN_FLINT = ITEMS.registerSimpleItem("broken_flint", new Item.Properties().rarity(Rarity.COMMON));

    public static final DeferredItem<Item> COPPER_POWDER = ITEMS.registerSimpleItem("copper_powder", new Item.Properties().rarity(Rarity.COMMON));

    public static final DeferredItem<Item> DIRTY_COPPER_POWDER = ITEMS.registerSimpleItem("dirty_copper_powder", new Item.Properties().rarity(Rarity.COMMON));

    public static final DeferredItem<Item> HUGE_ROCKETBOOSTER = ITEMS.registerSimpleItem("huge_rocketbooster", new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> ROCKETBOOSTER = ITEMS.registerSimpleItem("rocketbooster", new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final DeferredItem<Item> INCOMPLETE_NETHERITE_NUGGET = ITEMS.registerSimpleItem("incomplete_netherite_nugget", new Item.Properties().rarity(Rarity.UNCOMMON));

    // Creates a creative tab with the id "create_skyblock_addon:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("create_skyblock_addon", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.create_skyblock_addon")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> BROKEN_FLINT.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(BROKEN_FLINT.get());
        output.accept(COPPER_POWDER.get());
        output.accept(DIRTY_COPPER_POWDER);
        output.accept(HUGE_ROCKETBOOSTER);
        output.accept(ROCKETBOOSTER);
        output.accept(INCOMPLETE_NETHERITE_NUGGET);
        output.accept(STONE_CASING_ITEM);
    }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public create_skyblock_addon(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (create_skyblock_addon) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(STONE_CASING);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
