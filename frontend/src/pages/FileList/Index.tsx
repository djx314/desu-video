/** @jsx jsx */
import { jsx } from "@emotion/core"
import { useState } from "react"
import * as style from "./style"
import HeadBar from "./Components/HeadBar"

const getDocumentHeight = () => document.documentElement.clientHeight

const FileList = () => {
    const [docHeight, docHeightState] = useState(getDocumentHeight())

    window.addEventListener("resize", () => {
        docHeightState(getDocumentHeight())
    })
    
    return (
        <div css={style.bodyContent(docHeight)}>
            <HeadBar contentHeight={docHeight} />
            <div>dfgetrt</div>
            <div>dfgetrt</div>
            <div>dfgetrt</div>
            <div>dfgetrt</div>
            <div>dfgetrt</div>
        </div>
    )
}

export default FileList