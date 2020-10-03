import React from "react";
import ReactDOM from "react-dom";
import "antd/dist/antd.css";
import { BrowserRouter as Router, Route } from "react-router-dom";
// import Home from "./pages/Home";
import * as serviceWorker from "./serviceWorker";
import { ConfigProvider } from "antd";
import zhCN from "antd/es/locale/zh_CN";

ReactDOM.render(
  <ConfigProvider locale={zhCN}>
    123
    {/* <Router>
      <Route path="/" component={Home} />
    </Router> */}
  </ConfigProvider>,
  document.getElementById("root")
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
