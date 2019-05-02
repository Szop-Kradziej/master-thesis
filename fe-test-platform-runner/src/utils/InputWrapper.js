import React from "react";

const stopPropagation = (e) => e.stopPropagation();
const InputWrapper = ({children}) =>
    <div onClick={stopPropagation}>
        {children}
    </div>;

export default InputWrapper

