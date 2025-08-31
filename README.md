# RandomComplement (1.12)

Some fixes and content expansion

# What has this mod done so far?

 - For [AE2UEL](https://github.com/AE2-UEL/Applied-Energistics-2):
   - Allows players to use the vanilla PickBlock Key to retrieve items from their Wireless Terminal's network and place them directly into their hand. Just like in Creative Mode!(Of course, items will be deducted from your network)
   - Ported the new version of AE2's additional rendering effects for craftable items,Also supports AE2FCR's Fluid Pattern Terminal.
   - Automatically fill blank patterns into the Pattern Terminal.
   - Ported nearly all features from the 1.19+ Inscriber; 
   - Changed the time display in Crafting Status to show elapsed time instead; 
   - Creative ME Storage Cell now maintains a capacity of 2^52-1.
   - The feature allows using JEI bookmarks to request crafting from AE networks or retrieve items from AE networks, which can be configured via keybindings in the controls menu. When not in an AE GUI interface, requests will be directed to carried Wireless Terminals. If a single network cannot provide sufficient items, the system will automatically search other connected Wireless Terminals.
 - For [MMCE](https://github.com/NovaEngineering-Source/ModularMachinery-Community-Edition):
   - ME Machinery Pattern Provider joins AE2 interface terminal! While custom naming is not supported, the ME Machinery Pattern Provider within the Interface Terminal will dynamically render its designated name from the Modular Machinery.
   - Conducted bug fixes for the compatibility between MMCE Structure Preview and the Pattern Terminal support.
 - For [Botania](https://github.com/VazkiiMods/Botania):
   - Allow flor geradora to directly link to the mana pool
   - Allow the use of sparks on Runic Alta
   - Allow the use of sparks on Botanical Brewery
   - By default, they are turned off and you need to go to config to open them yourself
 - Fix bug in [AE2FluidCraft-Rework](https://github.com/Circulate233/AE2FluidCraft-Rework) ([#175](https://github.com/AE2-UEL/AE2FluidCraft-Rework/issues/175),PR Already created,After the version update, it will be removed)
 - Related projects with [NotEnoughEnergistics](https://github.com/vfyjxf/NotEnoughEnergistics):
   - The compatibility with [Extended Crafting Terminals for Applied Energistics 2](https://github.com/0xC4DE/Extended-Crafting-Terminals-For-AE2) has been added. Now you can use Ctrl+click on [+] to request the items.
   - Fixed an issue where the [AE2UEL](https://github.com/AE2-UEL/Applied-Energistics-2) Wireless Crafting Terminal placed in the bauble slot could not request items when opened with the hotkey and using Ctrl+click [+], (this might have broken compatibility with the Wireless Crafting Terminal from WCT, not yet tested).
 - Set the Package Crafter in the extension of [PackagedAuto](https://github.com/TheLMiffy1111/PackagedAuto) (such as [PackagedExCrafting](https://github.com/TheLMiffy1111/PackagedExCrafting)) to the corresponding Recipe Catalyst of JEIRecipe.
 - Set the Terminal in the [Extended Crafting Terminals for Applied Energistics 2](https://github.com/0xC4DE/Extended-Crafting-Terminals-For-AE2) to the corresponding Recipe Catalyst of JEIRecipe.(1.0.1)
- Added a configuration option to disable the build permission for AE2's ME Security Terminal, as this feature provides no practical benefit and can be inconvenient. :)
- Fixed a bug in the lazy AE2 ME Level Maintainer that caused items crafted automatically to disappear partially.But this is just a temporary patch and it hasn't completely fixed the problem.(Can be closed in config)
- Prevent the Thermal Expansion Cyclic Assembler from operating when the output slot contains items to avoid potential bugs(Can be closed in config)
- Allow configuration of the effects of IC2's Energy Storage and Overclock Upgrades(Adjust in config)
- Fix the erroneous behavior of [MMCE](https://github.com/NovaEngineering-Source/ModularMachinery-Community-Edition) structure preview in JEI

You can freely add it to your modpack，as long as you indicate the source
