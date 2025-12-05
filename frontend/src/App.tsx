import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import PDV from './pages/PDV';
import Cadastros from './pages/Cadastros';
import Vendas from './pages/Vendas';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/pdv" element={<PDV />} />
        <Route path="/cadastros" element={<Cadastros />} />
        <Route path="/vendas" element={<Vendas />} />
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
