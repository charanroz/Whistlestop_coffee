const isLocalHost = (host) =>
  host === "localhost" ||
  host === "127.0.0.1" ||
  host.startsWith("192.168.") ||
  host.startsWith("10.") ||
  /^172\.(1[6-9]|2\d|3[0-1])\./.test(host);

const host = window.location.hostname;

const API = isLocalHost(host)
  ? `http://${host === "localhost" ? "localhost" : host}:8080`
  : "https://whistlestop-coffee.onrender.com";

export default API;
