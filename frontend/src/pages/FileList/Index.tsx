/** @jsx jsx */
import { jsx } from "@emotion/core"
import { useState } from "react"
import * as style from "./style"
import { getBarHeight, HeadBar } from "./Components/HeadBar"
import { FileListContent } from "./Components/FileListContent"
import Axios from "axios"

const getDocumentHeight = () => document.documentElement.clientHeight

const FileList = () => {
    const [docHeight, docHeightState] = useState(getDocumentHeight())

    window.addEventListener("resize", () => {
        docHeightState(getDocumentHeight())
    })
    

    Axios

    return (
        <div css={style.bodyContent(docHeight)}>
            <HeadBar contentHeight={docHeight} />
            <FileListContent listHeight={docHeight - getBarHeight(docHeight)} />
        </div>
    )
}

export default FileList