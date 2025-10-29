export const MENU_ITEMS = {
  ADMIN: [
    { to: "/", label: "Dashboard" },
    { to: "/productos", label: "Productos" },
    { to: "/pedidos", label: "Pedidos" },
    { to: "/distribuciones", label: "Distribuciones" },
    { to: "/maquilados", label: "Maquilados" },
    { to: "/usuarios", label: "Usuarios" },
  ],
  VENTAS: [
    { to: "/", label: "Dashboard" },
    { to: "/pedidos", label: "Pedidos" },
    { to: "/productos", label: "Productos" },
  ],
  MAQUILA: [
    { to: "/", label: "Dashboard" },
    { to: "/maquilados", label: "Maquilados" },
  ],
} as const;
