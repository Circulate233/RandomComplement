package mezz.jei.transfer;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class RecipeTransferErrorInternal implements IRecipeTransferError {
	public static final RecipeTransferErrorInternal INSTANCE = new RecipeTransferErrorInternal();

	private RecipeTransferErrorInternal() {

	}

	@Override
	public @NotNull Type getType() {
		return Type.INTERNAL;
	}

    @Override
    public void showError(@NotNull Minecraft minecraft, int i, int i1, @NotNull IRecipeLayout iRecipeLayout, int i2, int i3) {

    }

	public void showError(int mouseX, int mouseY, IRecipeLayout recipeLayout, int recipeX, int recipeY) {

	}
}
