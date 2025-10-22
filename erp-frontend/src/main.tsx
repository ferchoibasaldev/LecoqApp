import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import "./style.css";

import { AuthProvider } from "./auth/AuthContext";
import PrivateRoute from "./auth/PrivateRoute";
import RoleGuard from "./auth/RoleGuard";

import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Productos from "./pages/Productos";
import Pedidos from "./pages/Pedidos";
import Distribuciones from "./pages/Distribuciones";
import Maquilados from "./pages/Maquilados";
import Usuarios from "./pages/Usuarios";

function App() {
  return (
    <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route path="/" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
          <Route path="/productos" element={<PrivateRoute><Productos /></PrivateRoute>} />

          <Route path="/pedidos" element={
            <PrivateRoute>
              <RoleGuard roles={["ADMIN","VENTAS"]}><Pedidos /></RoleGuard>
            </PrivateRoute>
          } />

          <Route path="/distribuciones" element={
            <PrivateRoute>
              <RoleGuard roles={["ADMIN","VENTAS"]}><Distribuciones /></RoleGuard>
            </PrivateRoute>
          } />

          <Route path="/maquilados" element={
            <PrivateRoute>
              <RoleGuard roles={["ADMIN","MAQUILA"]}><Maquilados /></RoleGuard>
            </PrivateRoute>
          } />

          <Route path="/usuarios" element={
            <PrivateRoute>
              <RoleGuard roles={["ADMIN"]}><Usuarios /></RoleGuard>
            </PrivateRoute>
          } />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
  );
}

const rootEl = document.getElementById("root");
if (!rootEl) throw new Error("Falta #root en index.html");
ReactDOM.createRoot(rootEl).render(<React.StrictMode><App /></React.StrictMode>);
