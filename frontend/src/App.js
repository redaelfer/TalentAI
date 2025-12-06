import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./components/Login";
import SignUp from "./components/SignUp";
import CandidateDashboard from "./components/CandidateDashboard";
import RhDashboard from "./components/RhDashboard";
import RhKanban from "./components/RhKanban"; // <-- IMPORT

const PrivateRoute = ({ children }) => {
  const userId = localStorage.getItem("userId");
  return userId ? children : <Navigate to="/login" />;
};

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={
            <PrivateRoute>
              <CandidateDashboard />
            </PrivateRoute>
          } />

          <Route path="/login" element={<Login />} />
          <Route path="/sign-up" element={<SignUp />} />

          <Route path="/rh" element={
            <PrivateRoute>
              <RhDashboard />
            </PrivateRoute>
          } />

          <Route path="/rh/kanban" element={
            <PrivateRoute>
              <RhKanban />
            </PrivateRoute>
          } />

        </Routes>
      </div>
    </Router>
  );
}

export default App;