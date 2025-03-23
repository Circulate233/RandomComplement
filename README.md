# RandomComplement (1.12)

Some fixes and content expansion

# What has this mod done so far?

 - For [AE2UEL](https://github.com/AE2-UEL/Applied-Energistics-2):
   - Allows players to use the vanilla PickBlock Key to retrieve items from their Wireless Terminal's network and place them directly into their hand. Just like in Creative Mode!(Of course, items will be deducted from your network)
   - Ported the new version of AE2's additional rendering effects for craftable items,Also supports AE2FCR's Fluid Pattern Terminal.
   - Automatically fill blank patterns into the Pattern Terminal.

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
