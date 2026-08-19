"use client";

import { useEffect, useState, type ChangeEvent } from "react";

export function CoverImageInput() {
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [fileName, setFileName] = useState<string | null>(null);

  useEffect(() => {
    return () => {
      if (previewUrl) URL.revokeObjectURL(previewUrl);
    };
  }, [previewUrl]);

  function selectFile(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0] ?? null;

    if (previewUrl) URL.revokeObjectURL(previewUrl);
    setPreviewUrl(file ? URL.createObjectURL(file) : null);
    setFileName(file?.name ?? null);
  }

  return (
    <label style={styles.wrapper}>
      <span style={styles.label}>Kapak görseli</span>
      <input
        name="cover_image_file"
        type="file"
        accept="image/jpeg,image/png,image/webp"
        onChange={selectFile}
        style={styles.input}
      />
      <span style={styles.hint}>JPEG, PNG veya WebP · En fazla 5 MB</span>
      {previewUrl ? (
        <figure style={styles.preview}>
          {/* Blob URLs are local previews and cannot be handled by next/image. */}
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img src={previewUrl} alt="Seçilen kapak görseli önizlemesi" style={styles.image} />
          <figcaption style={styles.caption}>{fileName}</figcaption>
        </figure>
      ) : null}
    </label>
  );
}

const styles = {
  wrapper: {
    display: "grid",
    gap: 8
  },
  label: {
    color: "#081a40",
    fontWeight: 800
  },
  input: {
    width: "100%",
    padding: "12px 14px",
    border: "1px solid #d5def0",
    borderRadius: 16,
    background: "#f8faff",
    color: "#081a40",
    font: "inherit"
  },
  hint: {
    color: "#64708f",
    fontSize: 13
  },
  preview: {
    overflow: "hidden",
    margin: 0,
    border: "1px solid #d5def0",
    borderRadius: 18,
    background: "#eef3ff"
  },
  image: {
    display: "block",
    width: "100%",
    height: 190,
    objectFit: "cover" as const
  },
  caption: {
    overflow: "hidden",
    padding: "10px 12px",
    color: "#53617f",
    fontSize: 13,
    textOverflow: "ellipsis",
    whiteSpace: "nowrap" as const
  }
};
