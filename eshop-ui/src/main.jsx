import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

// Redux
import { Provider } from "react-redux";
import { store } from "./store/store.js";

createRoot(document.getElementById('root')).render(
  <StrictMode>
    {/* Provider = κάνει διαθέσιμο το Redux store σε όλη την React εφαρμογή */}
    <Provider store={store}>
      
      <ThemeProvider>
        <AppRouter />
      </ThemeProvider>
    </Provider>
  </StrictMode>,
)
