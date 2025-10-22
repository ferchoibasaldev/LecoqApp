import Layout from "../components/Layout";
import PageHeader from "../components/PageHeader";

const Card = ({ title, value }: { title: string; value: string }) => (
  <div className="border rounded-2xl p-4 bg-white shadow-sm">
    <div className="text-sm text-gray-500">{title}</div>
    <div className="text-2xl font-semibold mt-1">{value}</div>
  </div>
);

export default function Dashboard() {
  return (
    <Layout>
      <PageHeader title="Dashboard" subtitle="Resumen de tu operación hoy" />
      <section className="grid gap-4 md:grid-cols-4">
        <Card title="Productos activos" value="—" />
        <Card title="Pedidos hoy" value="—" />
        <Card title="Distribuciones en curso" value="—" />
        <Card title="Órdenes de maquilado" value="—" />
      </section>

      <section className="mt-6 grid gap-4 md:grid-cols-2">
        <div className="border rounded-2xl p-4 bg-white shadow-sm">
          <h3 className="font-semibold mb-3">Últimos pedidos</h3>
          <div className="text-sm text-gray-500">No hay datos aún.</div>
        </div>
        <div className="border rounded-2xl p-4 bg-white shadow-sm">
          <h3 className="font-semibold mb-3">Bajo stock</h3>
          <div className="text-sm text-gray-500">Todo en orden.</div>
        </div>
      </section>
    </Layout>
  );
}
