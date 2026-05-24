import axios from "axios";

export type WikiResult = {
  title: string;
  extract: string;
  url: string;
  lang: string;
};

async function fetchSummary(lang: string, query: string): Promise<WikiResult | null> {
  const endpoint = `https://${lang}.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(query)}`;
  try {
    const res = await axios.get(endpoint, { timeout: 6000 });
    if (res.status !== 200 || !res.data?.extract) return null;
    return {
      title: res.data.title,
      extract: res.data.extract,
      url: res.data.content_urls?.desktop?.page ?? endpoint,
      lang,
    };
  } catch {
    return null;
  }
}

export async function wikiSearch(query: string): Promise<WikiResult | null> {
  if (!query.trim()) return null;
  const langs = ["es", "en"];
  for (const lang of langs) {
    const result = await fetchSummary(lang, query);
    if (result) return result;
  }
  return null;
}
