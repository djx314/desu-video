export const getTime = (timestamp?: string) => {
  let now = new Date();
  if (timestamp) {
    now = new Date(timestamp);
  }
  const year = now.getFullYear();
  const month = addZero(now.getMonth() + 1);
  const date = addZero(now.getDate());
  const hours = addZero(now.getHours());
  const minutes = addZero(now.getMinutes());
  const seconds = addZero(now.getSeconds());
  return `${year}-${month}-${date} ${hours}:${minutes}:${seconds}`;
};

export const generateUrlWithParams = (url: string, params: any) => {
  let urlParams = [];
  for (let key in params) {
    urlParams.push(`${key}=${encodeURI(params[key])}`);
  }
  url += "?" + urlParams.join("&");
  return url;
};

export const nullToEmptyString = (x: any) => {
  if (x === null || x === undefined) {
    return "";
  }
  return x;
};

export const nullToBoolean = (x: any) => {
  if (x === null || x === undefined || x === false) {
    return false;
  }
  return true;
};

export const changeSizeFormat = (size: string) => {
  let formatSize = "";
  const limit = Number(size);
  if (limit < 0.1 * 1024) {
    formatSize = limit.toFixed(2) + "B"; //小于0.1KB，则转化成B
  } else if (limit < 0.1 * 1024 * 1024) {
    formatSize = (limit / 1024).toFixed(2) + "KB"; //小于0.1MB，则转化成KB
  } else if (limit < 0.1 * 1024 * 1024 * 1024) {
    formatSize = (limit / (1024 * 1024)).toFixed(2) + "MB"; //小于0.1GB，则转化成MB
  } else {
    formatSize = (limit / (1024 * 1024 * 1024)).toFixed(2) + "GB"; //其他转化成GB
  }
  let sizeStr = formatSize + ""; //转成字符串
  let index = sizeStr.indexOf("."); //获取小数点处的索引
  let dou = sizeStr.substr(index + 1, 2); //获取小数点后两位的值
  if (dou === "00") {
    //判断后两位是否为00，如果是则删除00
    return sizeStr.substring(0, index) + sizeStr.substr(index + 3, 2);
  }
  return formatSize;
};

const addZero = (x: number) => {
  if (x < 10) {
    return `0${x}`;
  }
  return x;
};

export const booleanToString = (x: boolean) => {
  if (x) {
    return "是";
  }
  return "否";
};

export const removeHtmlTag = (x: string) => {
  const reg = /((<\w.*?>)|(<\/\w.*?>))/g;
  return x.replace(reg, "");
};

export const changeNanToDefaultNum = (x: any, defaultValue: number) => {
  if (typeof x === 'number') {
    return x;
  }
  return defaultValue;
}
