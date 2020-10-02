export interface IReqData {
  id: number;
  name: string;
  rootUrl: string;
  minuteLimit: number;
  maxOccurs: string;
  maxDeep: string;
  lastestRunTime: string;
  isRunning?: boolean;
  isFinish?: boolean;
}

export interface IResData {
  key?: number;
  id: number;
  name: string;
  rootUrl: string;
  maxDeep: number;
  minuteLimit: number;
  maxOccurs: number;
  lastestRunTime: string;
}

export interface IModalStateProps {
  loading: boolean;
  visible: boolean;
  handleModalLoading: Function;
  handleModalVisible: Function;
  handleModalLoadingCancle: Function;
  handleModalVisibleCancle: Function;
  refreshData: Function;
  modifyPlanID?: number;
}
