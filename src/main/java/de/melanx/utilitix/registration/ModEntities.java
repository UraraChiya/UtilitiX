package de.melanx.utilitix.registration;

import de.melanx.utilitix.content.track.carts.*;
import io.github.noeppi_noeppi.libx.annotation.registration.RegisterClass;

@RegisterClass
public class ModEntities {

    public static final Cart.CartType<EnderCart> enderCart = Cart.type("ender_cart", EnderCart::new);
    public static final Cart.CartType<PistonCart> pistonCart = Cart.type("piston_cart", PistonCart::new);
    public static final Cart.CartType<StonecutterCart> stonecutterCart = Cart.type("stonecutter_cart", StonecutterCart::new);
    public static final Cart.CartType<AnvilCart> anvilCart = Cart.type("anvil_cart", AnvilCart::new);
}
