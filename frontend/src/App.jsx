import { Routes, Route, Navigate } from 'react-router-dom'
import Navbar from './components/Navbar'
import RotaPrivada from './components/RotaPrivada'
import Login from './pages/Login'
import Cadastro from './pages/Cadastro'
import Inicio from './pages/Inicio'
import MeusDados from './pages/MeusDados'
import Pedidos from './pages/Pedidos'
import PedidoForm from './pages/PedidoForm'
import AvaliarPedido from './pages/AvaliarPedido'
import GerarContrato from './pages/GerarContrato'
import Automoveis from './pages/Automoveis'
import Clientes from './pages/Clientes'
import Contratos from './pages/Contratos'

function App() {
  return (
    <>
      <Navbar />
      <main className="container">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/cadastro" element={<Cadastro />} />

          <Route path="/" element={<RotaPrivada><Inicio /></RotaPrivada>} />
          <Route path="/pedidos" element={<RotaPrivada><Pedidos /></RotaPrivada>} />
          <Route path="/automoveis" element={<RotaPrivada><Automoveis /></RotaPrivada>} />
          <Route path="/contratos" element={<RotaPrivada><Contratos /></RotaPrivada>} />
          <Route path="/pedidos/:id/editar" element={<RotaPrivada><PedidoForm /></RotaPrivada>} />

          <Route path="/meus-dados" element={<RotaPrivada perfil="CLIENTE"><MeusDados /></RotaPrivada>} />
          <Route path="/pedidos/novo" element={<RotaPrivada perfil="CLIENTE"><PedidoForm /></RotaPrivada>} />

          <Route path="/clientes" element={<RotaPrivada perfil="AGENTE"><Clientes /></RotaPrivada>} />
          <Route path="/pedidos/:id/avaliar" element={<RotaPrivada perfil="AGENTE"><AvaliarPedido /></RotaPrivada>} />
          <Route path="/pedidos/:id/contrato" element={<RotaPrivada perfil="AGENTE"><GerarContrato /></RotaPrivada>} />

          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </main>
    </>
  )
}

export default App
