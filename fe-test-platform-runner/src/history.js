//https://github.com/ReactTraining/react-router/blob/master/FAQ.md#how-do-i-access-the-history-object-outside-of-components

import createBrowserHistory from "history/createBrowserHistory";

const customHistory = createBrowserHistory({basename: process.env.ROUTER_BASE_PATH});
export default customHistory;