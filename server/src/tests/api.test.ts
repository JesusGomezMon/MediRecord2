import request from "supertest";

let app: import("express").Express;

beforeAll(async () => {
  process.env.DB_PATH = ":memory:";
  app = (await import("../index")).default;
});

describe("API smoke tests", () => {
  it("health returns stats", async () => {
    const res = await request(app).get("/health");
    expect(res.status).toBe(200);
    expect(res.body.ok).toBe(true);
    expect(res.body.stats).toBeDefined();
  });

  it("creates and lists medications", async () => {
    const med = {
      name: "Ibuprofeno 400mg",
      description: "Para dolor moderado",
      source: "test",
    };
    const created = await request(app).post("/medications").send(med);
    expect(created.status).toBe(201);
    expect(created.body.id).toBeDefined();
    const list = await request(app).get("/medications");
    const names = list.body.map((m: any) => m.name);
    expect(names).toContain("Ibuprofeno 400mg");
  });
});
