import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./components/Login";
import SignUp from "./components/SignUp"; // Assurez-vous que ce fichier existe
import CandidateDashboard from "./components/CandidateDashboard";
import RhDashboard from "./components/RhDashboard";

// Petit composant pour protéger les pages (redirection vers login si non connecté)
const PrivateRoute = ({ children }) => {
  const userId = localStorage.getItem("userId");
  return userId ? children : <Navigate to="/login" />;
};

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          {/* Route par défaut : Dashboard Candidat */}
          <Route path="/" element={
            <PrivateRoute>
              <CandidateDashboard />
            </PrivateRoute>
          } />

          {/* Routes d'authentification */}
          <Route path="/login" element={<Login />} />

          {/* --- C'EST CETTE LIGNE QUI MANQUAIT POUR L'INSCRIPTION --- */}
          <Route path="/sign-up" element={<SignUp />} />

          {/* Dashboard RH */}
          <Route path="/rh" element={
            <PrivateRoute>
              <RhDashboard />
            </PrivateRoute>
          } />
        </Routes>
      </div>
    </Router>
  );
}

export default App;